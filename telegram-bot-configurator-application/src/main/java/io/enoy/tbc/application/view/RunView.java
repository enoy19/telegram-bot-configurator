/**
 * 
 */
package io.enoy.tbc.application.view;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.TaskProgressView;
import org.controlsfx.control.ToggleSwitch;
import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import io.enoy.tbc.application.control.enoyfx.RootFxmlLoader;
import io.enoy.tbc.application.control.concurrent.BotMessageHandler;
import io.enoy.tbc.application.control.concurrent.TaskHandler;
import io.enoy.tbc.application.control.concurrent.UserHandler.CommandTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Enis.Oezsoy
 */
@Component
public class RunView extends VBox implements TaskHandler
{

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private ResourcePropertyBundle bundle;

	@Autowired
	private BotMessageHandler botMessageHandler;

	@Autowired
	private RootFxmlLoader rootFxmlLoader;

	@FXML
	private ToggleSwitch toggleSwitch;

	@FXML
	private Label labelTimeStarted;

	@FXML
	private Label labelTimeStopped;

	@FXML
	private Label labelTimeRun;

	@FXML
	private Label labelAmountCommands;

	@FXML
	private TaskProgressView<Task<?>> taskProgressView;

	private IntegerProperty commandCount;

	private LongProperty secondsRun;

	@PostConstruct
	public void init()
	{
		rootFxmlLoader.load(this, bundle.getResourceBundle(), "/fxml/TBCRun.fxml");

		commandCount = new SimpleIntegerProperty(0);
		secondsRun = new SimpleLongProperty(0);
		labelAmountCommands.textProperty().bind(commandCount.asString());

		labelTimeRun.textProperty().bind(Bindings.createStringBinding(() -> {
			long millis = secondsRun.get() * 1000;
			return String.format("%02d:%02d:%02d", //
				TimeUnit.MILLISECONDS.toHours(millis), //
				TimeUnit.MILLISECONDS.toMinutes(millis) - //
			TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), //
				TimeUnit.MILLISECONDS.toSeconds(millis) - //
			TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		}, secondsRun));

		final PauseTransition secondsCounter = new PauseTransition(Duration.seconds(1));
		secondsCounter.setOnFinished(e -> {
			if (botRunning().get())
			{
				secondsRun.set(secondsRun.get() + 1);
				secondsCounter.play();
			}
		});

		final PauseTransition ptStop = new PauseTransition(Duration.millis(500));
		ptStop.setOnFinished(e -> {
			if (botMessageHandler.isRunning())
			{
				ptStop.play();
			}
			else
			{
				toggleSwitch.setDisable(false);
			}
		});

		final PauseTransition ptStart = new PauseTransition(Duration.millis(500));
		ptStart.setOnFinished(e -> {
			toggleSwitch.setDisable(false);
		});

		botRunning().addListener((v, o, n) -> {
			toggleSwitch.setDisable(true);
			if (n)
			{
				secondsRun.set(0);
				labelTimeStarted.setText(FORMAT.format(new Date()));
				labelTimeStopped.setText("-");
				secondsCounter.playFromStart();
				ptStart.play();
				Thread botMessageHandlerThread = new Thread(() -> {
					try
					{
						botMessageHandler.call();
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
					finally
					{
						Platform.runLater(()->{
							toggleSwitch.setSelected(false);
						});
					}
				}, "Bot Thread");
				botMessageHandlerThread.setDaemon(true);
				botMessageHandlerThread.start();

			}
			else
			{
				labelTimeStopped.setText(FORMAT.format(new Date()));
				secondsCounter.stop();
				ptStop.play();
			}
		});

		botMessageHandler.stopRequestedProperty().bind(botRunning().not());

	}

	public ReadOnlyBooleanProperty botRunning()
	{
		return toggleSwitch.selectedProperty();
	}

	public void stopBot()
	{
		toggleSwitch.setSelected(false);
	}

	@Override
	public ObjectProperty<TelegramBotTask> handleTask(final CommandTask task)
	{
		ObjectProperty<TelegramBotTask> tbt = new SimpleObjectProperty<>(null);
		Platform.runLater(() -> {
			TelegramBotTask botTask = new TelegramBotTask(task);
			taskProgressView.getTasks().add(botTask);
			commandCount.set(commandCount.get() + 1);
			tbt.set(botTask);
		});

		return tbt;
	}

}
