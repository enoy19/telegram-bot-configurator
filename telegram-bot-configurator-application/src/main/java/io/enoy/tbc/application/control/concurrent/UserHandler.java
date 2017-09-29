/**
 * 
 */
package io.enoy.tbc.application.control.concurrent;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ReplyKeyboardHide;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import javafx.beans.property.ObjectProperty;
import io.enoy.tbc.application.control.*;
import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import io.enoy.tbc.application.control.js.functions.ApplySaveFile;
import io.enoy.tbc.application.control.js.functions.GetSaveFile;
import io.enoy.tbc.application.control.js.functions.Log;
import io.enoy.tbc.application.control.js.functions.Sleep;
import io.enoy.tbc.application.control.utils.CommandLineUtils;
import io.enoy.tbc.application.control.utils.HelpUtils;
import io.enoy.tbc.application.control.utils.KeyboardUtils;
import io.enoy.tbc.application.model.*;
import io.enoy.tbc.application.model.input.UserInput;
import io.enoy.tbc.application.model.input.UserInput.UserInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Enis.Oezsoy
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserHandler implements MessageSender {

	public static final long IDLE_MAX = 1000 * 60 * 5; // 5 Minutes TODO:
														// Configuration
	// public static final long IDLE_MAX = 1000 * 20; // 20 Seconds
	private static final ScriptEngineManager ENGINE_MANAGER = new ScriptEngineManager();

	@Autowired
	private ResourcePropertyBundle bundle;
	@Autowired
	private ObservableDataContainer dataContainer;
	@Autowired
	private CommandExecuter commandExecuter;
	@Autowired
	@Qualifier("bot")
	private ObjectProperty<TelegramBot> bot;
	@Autowired
	private CommandLineUtils commandHelper;
	@Autowired
	private HelpUtils helpUtils;
	@Autowired
	private CommandParser commandParser;
	@Autowired
	private GetSaveFile getSaveFile;
	@Autowired
	@Qualifier("applySaveFileFunction")
	private ApplySaveFile applySaveFile;

	private User user;

	private Command currentCommand;
	private List<UserInput> currentArgumentInputs;

	private long lastInteraction;

	@PostConstruct
	public void init() {
		currentArgumentInputs = new ArrayList<>();
	}

	// TODO: localized messages

	public void handleUserInput(UserInput userInput, long chatId) {
		this.handleUserInput(userInput, chatId, false);
	}

	public boolean handleUserInput(UserInput userInput, long chatId, boolean silent) {
		updateLastInteraction();

		if (!user.isActivated()) {
			if (!silent)
				sendMessage(bundle.get("activated.not").get());
			return false;
		}

		// no current command
		if (currentCommand == null) {
			if (!validCommand(userInput)) {
				return false;
			}
		}

		if (currentCommand != null) {
			int currentArgumentPosition = currentArgumentInputs.size();

			currentArgumentInputs.add(currentArgumentPosition, userInput);
			if (currentArgumentPosition > 0) {
				if (!validArgument(userInput, currentArgumentPosition, silent)) {
					return false;
				}
			} else {
				// current userinput is command or commandline
				List<String> commandLine = commandParser.parseCommand((String) userInput.getData());
				if (commandLine.size() > 1) {
					boolean valid = true;

					if(dataContainer.getConfig().getValue().isCollectLastArguments()){
						if (commandLine.size() - (currentArgumentPosition == 0 ? 1 : 0) > currentCommand.getArguments()
								.size()) {
							// more args then defined
							
							String arg;
							int offset = 0;
							if (currentArgumentPosition == 0) {
								// command included
								offset++;
							}
							offset += currentCommand.getArguments().size() - 1;
							// if(currentArgumentPosition)
							arg = commandLine.subList(offset, commandLine.size()).stream().collect(Collectors.joining(" "));
							
							commandLine.removeAll(commandLine.subList(offset, commandLine.size()));
							commandLine.add(arg);
						}
					}

					int size = Math.min(commandLine.size() - 1, currentCommand.getArguments().size());
					for (int i = 0; i < size - 1; i++) {
						UserInput tmpInput = UserInput.text(commandLine.get(i + 1));
						valid = this.handleUserInput(tmpInput, chatId, true);
						if (!valid)
							break;
					}
					if (valid) {
						UserInput tmpInput = UserInput.text(commandLine.get(size));
						this.handleUserInput(tmpInput, chatId);
					}
					return false;
				}
			}

			int nextArgumentPos = currentArgumentPosition + 1;

			checkDefinitions(nextArgumentPos, silent, chatId);
		}

		return true;

	}

	private void updateLastInteraction() {
		lastInteraction = System.currentTimeMillis();
	}

	private void checkDefinitions(int nextArgumentPos, boolean silent, long chatId) {
		ArgumentDefinition definition = currentCommand.getArgumentDefinition(nextArgumentPos);

		if (definition != null) {
			// Set<Argument> definedArguments =
			// currentCommand.getArguments(nextArgumentPos);
			Set<String> availableArguments = getAvailableArguments(nextArgumentPos);

			if (availableArguments != null) {
				availableArguments = availableArguments.stream()//
						.filter(s -> !s.matches("^%\\w+%$"))//
						.collect(Collectors.toSet());

				String message = definition.getName() + ":";
				if (availableArguments.size() > 0) {
					if (!silent)
						sendKeyBoardMessage(message, availableArguments);
				} else {
					if (!silent)
						sendKeyBoardMessage(message);
				}

			} else {
				if (!silent)
					sendMessageHideKeyBoard(bundle.get("permissions.insufficient").get());
				clearCurrentCommand();
			}
		} else {
			// No more definitions... execute

			// sendMessageHideKeyBoard("Executing...");
			Argument[] arguments = getArguments();
			CommandTask commandTask = new CommandTask(currentCommand, arguments, currentArgumentInputs, chatId);
			clearCurrentCommand();
			commandExecuter.submit(commandTask);
		}
	}

	private boolean validCommand(UserInput userInput) {
		currentArgumentInputs.clear();

		if (userInput.getType() == UserInputType.TEXT) {
			String userText = (String) userInput.getData();
			final String text = userText.startsWith("/") ? userText.substring(1) : userText;

			if (helpUtils.isHelpCommand(text)) {
				sendMessage(helpUtils.getHelpText(user, dataContainer.getCommands()));
				return false;
			}

			Optional<Command> optCommand = dataContainer.getCommands().stream()//
					.filter(c -> text.startsWith(c.getCommand()))//
					.findAny();

			if (optCommand.isPresent()) {
				Command command = optCommand.get();
				if (user.isPermittedFor(command)) {
					currentCommand = command;
				} else {
					sendMessage(bundle.get("command.allowed.not").get());
					return false;
				}
			} else {
				sendMessage(bundle.get("command.found.not").get());
				return false;
			}
		}

		return true;
	}

	private boolean validArgument(UserInput userInput, int currentArgumentPosition, boolean silent) {
		// check valid ARGUMENT (not command)
		boolean valid = false;
		Set<String> availableArguments = getAvailableArguments(currentArgumentPosition);

		if (availableArguments != null) {
			if (userInput.getType() == UserInputType.TEXT) {
				String text = (String) userInput.getData();
				if (text.equals(KeyboardUtils.ABORT)) {
					// ABORT!
					if (!silent)
						sendMessageHideKeyBoard(bundle.get("aborted").get());
					clearCurrentCommand();
					return false;
				}
				valid = availableArguments.size() == 0 //
						|| availableArguments.contains(text) //
						|| availableArguments.contains("%TEXT%");
			} else {
				String type = userInput.getType().name();
				if (availableArguments.contains("%" + type + "%")) {
					valid = true;
					sendMessageHideKeyBoard(bundle.get("ok").get());
				}
			}
		}

		if (!valid) {
			clearCurrentCommand();
			if (!silent)
				sendMessageHideKeyBoard(bundle.get("argument.invalid").get());
			return false;
		}
		return true;
	}

	private Argument[] getArguments() {
		// size - 1 because index 0 = the command itself
		int size = currentArgumentInputs.size() - 1;
		Argument[] arguments = new Argument[size];

		for (int i = 0; i < size; i++) {
			UserInput input = currentArgumentInputs.get(i + 1);
			if (input.getType() == UserInputType.TEXT) {
				Optional<Argument> optArg = currentCommand.getArgument(i + 1, (String) input.getData());
				if (optArg.isPresent()) {
					arguments[i] = optArg.get();
				}
			}
		}

		return arguments;
	}

	private void clearCurrentCommand() {
		currentCommand = null;
	}

	private Set<String> getAvailableArguments(int position) {
		Set<String> availableArguments = new HashSet<>();
		Set<String> definedArgumentsString = new HashSet<>();
		Set<String> restrictedArgumentsString = new HashSet<>();

		boolean defined;
		boolean restricted;

		Set<Permission> commandPermissions = user.getPermissionsFor(currentCommand);
		boolean noRestrictions = commandPermissions.stream()//
				.filter(p -> {
					if (p.getRestriction(position) != null) {
						return p.getRestriction(position).getRestrictions().size() == 0;
					}
					return true;
				})//
				.findAny().isPresent();

		restricted = !noRestrictions;

		Set<Argument> definedArguments = currentCommand.getArguments(position);

		defined = definedArguments != null && definedArguments.size() > 0;

		if (defined) {
			definedArguments.forEach(a -> definedArgumentsString.add(a.getArgument()));
			availableArguments.addAll(definedArgumentsString);
		}
		if (restricted) {
			commandPermissions.forEach(p -> {
				restrictedArgumentsString.addAll(p.getRestriction(position).getRestrictions());
			});
			availableArguments.addAll(restrictedArgumentsString);
		}
		if (restricted && defined) {
			Set<String> toBeRemoved = new HashSet<>();
			availableArguments.forEach(s -> {
				if (!restrictedArgumentsString.contains(s) || !definedArgumentsString.contains(s)) {
					toBeRemoved.add(s);
				}
			});
			availableArguments.removeAll(toBeRemoved);
			if (availableArguments.size() == 0) {
				return null;
			}
		}

		return availableArguments;

	}

	private SendResponse sendMessage(String message) {
		return bot.get().execute(new SendMessage(user.getId(), message));
	}

	private SendResponse sendMessageHideKeyBoard(String message) {
		return bot.get().execute(new SendMessage(user.getId(), message).replyMarkup(new ReplyKeyboardHide()));
	}

	private SendResponse sendKeyBoardMessage(String message, String... keys) {
		SendMessage sm = new SendMessage(user.getId(), message);
		sm.replyMarkup(KeyboardUtils.getKeyboard(keys));
		return bot.get().execute(sm);
	}

	private SendResponse sendKeyBoardMessage(String message, Collection<String> keys) {
		String[] k = (String[]) keys.toArray(new String[keys.size()]);
		return sendKeyBoardMessage(message, k);
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public TelegramBot getBot() {
		return bot.get();
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public Role getRole(final String roleName) {
		Optional<Role> optRole = dataContainer.getRoles().stream().filter(r -> r.getName().equals(roleName)).findAny();
		if (optRole.isPresent()) {
			return optRole.get();
		}
		return null;
	}

	@Override
	public User[] getUsersOfRole(final Role role) {
		List<User> users = dataContainer.getUsers().stream().filter(u -> u.getRoles().contains(role))
				.collect(Collectors.toList());
		return users.toArray(new User[users.size()]);
	}

	public void clearIfIdle(long time) {
		if (time - lastInteraction >= IDLE_MAX) {
			if (currentCommand != null) {
				sendMessageHideKeyBoard(bundle.get("idle.too.long").get());
				clearCurrentCommand();
			}
		}
	}

	public class CommandTask implements Runnable {

		private Command command;
		private ScriptEngine engine;
		private UiControlAdapter uiControlAdapter;

		public CommandTask(Command command, Argument[] arguments, List<UserInput> userInputs, long chatId) {
			this.command = command;
			this.engine = ENGINE_MANAGER.getEngineByName("nashorn");

			UserInput[] currentArgumentArray = userInputs.toArray(new UserInput[userInputs.size()]);

			Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

			bindings.put("config", dataContainer.getConfig().get());
			bindings.put("commands", (List<Command>) dataContainer.getCommands());
			bindings.put("permissions", (List<Permission>) dataContainer.getPermissions());
			bindings.put("roles", (List<Role>) dataContainer.getRoles());
			bindings.put("users", (List<User>) dataContainer.getUsers());
			// bindings.put("console", console);
			bindings.put("user", user);
			bindings.put("command", this.command);
			bindings.put("userInputs", currentArgumentArray);
			bindings.put("arguments", arguments);
			bindings.put("msg", (MessageSender) UserHandler.this);
			bindings.put("cmd", commandHelper);
			bindings.put("ui", new UiControlerAdapterImpl());
			bindings.put("chatId", chatId);

			// FUNCTIONS
			bindings.put("applySaveFile", applySaveFile);
			bindings.put("getSaveFile", getSaveFile);
			// TODO: Singleton
			bindings.put("log", new Log());
			bindings.put("sleep", new Sleep());
		}

		@Override
		public void run() {
			try {
				engine.eval(command.getScript());
				// sendMessage("Done!");
			} catch (Exception e) {
				e.printStackTrace();
				sendMessage("Unexpected Error occured!");
			}
		}

		public void setUiControlAdapter(UiControlAdapter uiControlAdapter) {
			this.uiControlAdapter = uiControlAdapter;
			this.uiControlAdapter.title(user.getName() + ": " + command.getCommand());
			Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

			bindings.put("ui", this.uiControlAdapter);
		}

	}

	public void handleExternalCommand(String externalOneLineCommand) {
		if (currentCommand == null) {
			updateLastInteraction();
			List<String> commandLine = commandParser.parseCommand(externalOneLineCommand);
			if (commandLine.size() > 0) {
				handleUserInput(UserInput.text(externalOneLineCommand), -1);
			}
		}
	}

	public boolean isBusy() {
		return currentCommand != null;
	}

	@Override
	public String getBotToken() {
		return dataContainer.getConfig().get().getBotToken();
	}

}
