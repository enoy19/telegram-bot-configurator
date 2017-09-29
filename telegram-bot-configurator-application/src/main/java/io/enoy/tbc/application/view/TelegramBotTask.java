/**
 * 
 */
package io.enoy.tbc.application.view;

import javafx.concurrent.Task;
import io.enoy.tbc.application.control.UiControlAdapter;
import io.enoy.tbc.application.control.concurrent.UserHandler.CommandTask;

/**
 * @author Enis.Oezsoy
 */
public class TelegramBotTask extends Task<Void> implements UiControlAdapter
{

	private CommandTask commandTask;

	public TelegramBotTask(CommandTask commandTask)
	{
		this.commandTask = commandTask;
		this.commandTask.setUiControlAdapter(this);

	}

	@Override
	protected Void call() throws Exception
	{
		commandTask.run();
		return null;
	}

	@Override
	public void title(String title)
	{
		this.updateTitle(title);
	}

	@Override
	public void message(String message)
	{
		this.updateMessage(message);
	}

	@Override
	public void progress(double workDone, double max)
	{
		this.updateProgress(workDone, max);
	}

	@Override
	public void progress(long workDone, long max)
	{
		this.updateProgress(workDone, max);
	}

}
