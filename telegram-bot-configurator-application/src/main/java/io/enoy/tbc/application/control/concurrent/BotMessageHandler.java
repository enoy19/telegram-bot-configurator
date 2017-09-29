/**
 * 
 */
package io.enoy.tbc.application.control.concurrent;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import io.enoy.tbc.application.control.MultiCounter;
import io.enoy.tbc.application.control.ObservableDataContainer;
import io.enoy.tbc.application.control.SaveManager;
import io.enoy.tbc.application.control.utils.ShutdownSaveHook;
import io.enoy.tbc.application.model.Configuration;
import io.enoy.tbc.application.model.Role;
import io.enoy.tbc.application.model.User;
import io.enoy.tbc.application.model.input.UserInput;
import javafx.beans.property.*;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author Enis.Oezsoy
 */
@Component
public class BotMessageHandler implements Callable<Void> {

	private static final File EXTERNAL_DIR = new File("tbc/ext/");

	@Autowired
	private ObservableDataContainer dataContainer;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private CommandExecuter commandExecuter;

	@Autowired
	@Qualifier("applySaveFile")
	private ObjectProperty<File> applySaveFile;

	@Autowired
	@Qualifier("bot")
	private ObjectProperty<TelegramBot> bot;

	@Autowired
	private ShutdownSaveHook shutdownSaveHook;

	@Autowired
	private SaveManager saveManager;

	private IntegerProperty offset;
	private BooleanProperty running;
	private BooleanProperty stopRequested;

	private Map<User, UserHandler> userHandlers;
	private Configuration config;
	private boolean ignoreVeryFirst;
	private OkHttpClient httpClient;

	private boolean autoSave = false;
	private boolean extCall = false;

	@PostConstruct
	private void init() {
		running = new SimpleBooleanProperty(false);
		stopRequested = new SimpleBooleanProperty(false);
		offset = new SimpleIntegerProperty(0);

		ignoreVeryFirst = true;

		userHandlers = new HashMap<>();
		httpClient = new OkHttpClient.Builder().build();
	}

	@Override
	public Void call() throws Exception {
		running.set(true);
		GetUpdatesResponse response = null;

		// TODO: Ignore first?

		try {
			TelegramBot bot = configure();
			
			MultiCounter multiCounter = setupMultiCounter();

			if (autoSave) {
				// TODO: Test this
				shutdownSaveHook.addHook();
			}

			while (!stopRequested.get()) {

				if (applySaveFile.get() != null) {
					if (commandExecuter.getRunningCommands() == 0) {
						// TODO: bot not loaded correctly
						// System.out.println("Applying new Save...");
						saveManager.load(applySaveFile.get());
						bot = configure();
						applySaveFile.set(null);
					} else {
						// System.out.println("Waiting for commands to finish to
						// apply save...");
						sleep(1000);
					}
					continue;
				}

				// UPDATE COUNTER
				multiCounter.update();

				try {
					response = bot.execute(new GetUpdates().offset(offset.get()).timeout(0));
				} catch (Exception e) {
					System.err.println("Error receiving telegram updates!");
					e.printStackTrace();
					sleep(5000);
					continue;
				}

				List<Update> updates = response.updates();

				if (updates.size() > 0) {
					response.updates().forEach(u -> {
						if (!u.updateId().equals(offset.get())) {
							offset.set(u.updateId());

							if (!ignoreVeryFirst) {
								handleMessage(u.message());
							} else {
								ignoreVeryFirst = false;
							}
						}
					});
				}

				sleep(100);
			}
		} finally {
			userHandlers.clear();
			running.set(false);
			if (autoSave) {
				saveManager.save();
				shutdownSaveHook.removeHook();
			}
		}

		return null;
	}

	private MultiCounter setupMultiCounter() {
		MultiCounter multiCounter = new MultiCounter();

		if (autoSave) {
			multiCounter.addTimedTask(() -> {
				saveManager.save();
			}, 60000);
		}

		if (extCall) {
			multiCounter.addTimedTask(() -> {
				for (File file : EXTERNAL_DIR.listFiles()) {
					if (file.isFile() && file.getName().endsWith(".txt") && file.length() > 0) {
						String userName = file.getName();
						userName = userName.substring(0, userName.lastIndexOf('.'));
						User user = getUser(null, userName);
						if (user != null) {
							handleExternalCommand(user, file);
						}
					}
				}
			}, 7500);
		}
		
		multiCounter.addTimedTask(()->{
			final long time = System.currentTimeMillis();
			synchronized (userHandlers) {
				userHandlers.forEach((k,v)->{
					synchronized (v) {	
						try{
							v.clearIfIdle(time);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		}, UserHandler.IDLE_MAX);
		
		return multiCounter;
	}

	private TelegramBot configure() {
		config = SerializationUtils.clone(dataContainer.getConfig().get());
		TelegramBot bot = TelegramBotAdapter.buildCustom(config.getBotToken(), httpClient);
		autoSave = config.isAutoSave() && saveManager.hasCurrentFile();
		extCall = config.isExternalCommandCall();

		if (config.isExternalCommandCall()) {
			if (!EXTERNAL_DIR.exists()) {
				EXTERNAL_DIR.mkdirs();
			}
		}

		bot.execute(new GetMe());
		this.bot.set(bot);
		return bot;
	}

	private void handleExternalCommand(User user, File file) {

		UserHandler userHandler = getUserHandler(user);

		if (!userHandler.isBusy()) {
			synchronized (userHandler) {
				try (Scanner scanner = new Scanner(file)) {
					String command;
					while (scanner.hasNextLine()) {
						command = scanner.nextLine();
						userHandler.handleExternalCommand(command);
					}
					Files.write(file.toPath(), new byte[] {}, StandardOpenOption.TRUNCATE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void handleMessage(Message message) {
		if(message == null){
			return;
		}
		String userId = Integer.toString(message.from().id());
		UserInput userInput = UserInput.getUserInputFromMessage(message);

		User user = getUser(message, userId);
		
		if (user != null) {
			handleUserInput(userInput, user, message.chat().id());
		}
	}

	private User getUser(Message message, String userId) {

		Optional<User> optUser = dataContainer.getUsers().stream()//
				.filter(u -> u.getId().equalsIgnoreCase(userId))//
				.findFirst();

		User user = null;

		if (optUser.isPresent()) {
			user = optUser.get();
		} else {
			if (config.isAddUserToListOnContact() && message != null) {
				Role defaultRole = config.getAddUserToListOnContactDefaultGroup();
				int defaultRoleIndex = dataContainer.getRoles().indexOf(defaultRole);
				if (defaultRoleIndex > -1) {
					defaultRole = dataContainer.getRoles().get(defaultRoleIndex);
				}
				String id = Integer.toString(message.from().id());
				String username = message.from().username();
				String firstName = message.from().firstName();
				String lastName = message.from().lastName();
				String name = username + " " + firstName + " " + lastName;
				name = name.trim();
				name += " (auto)";
				User autoUser = new User();

				if (defaultRole != null) {
					autoUser.getRoles().add(defaultRole);
				}

				autoUser.setId(id);
				autoUser.setName(name);
				autoUser.setActivated(config.isAddUserToListOnContactActivated());
				dataContainer.getUsers().add(autoUser);
				user = autoUser;
			}
		}
		return user;
	}

	private void handleUserInput(UserInput userInput, User user, long chatId) {
		UserHandler userHandler = getUserHandler(user);
		userHandler.handleUserInput(userInput, chatId);
	}

	private UserHandler getUserHandler(User user) {
		UserHandler userHandler = null;
		if (!userHandlers.containsKey(user)) {
			userHandler = applicationContext.getBean(UserHandler.class);
			userHandler.setUser(user);
			userHandlers.put(user, userHandler);
		} else {
			userHandler = userHandlers.get(user);
		}

		return userHandler;
	}

	public BooleanProperty runningProperty() {
		return this.running;
	}

	public boolean isRunning() {
		return this.runningProperty().get();
	}

	public void setRunning(final boolean running) {
		this.runningProperty().set(running);
	}

	public BooleanProperty stopRequestedProperty() {
		return this.stopRequested;
	}

	public boolean isStopRequested() {
		return this.stopRequestedProperty().get();
	}

	public void setStopRequested(final boolean stopRequested) {
		this.stopRequestedProperty().set(stopRequested);
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
