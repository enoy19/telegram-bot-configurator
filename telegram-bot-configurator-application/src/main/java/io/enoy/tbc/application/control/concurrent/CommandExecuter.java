/**
 * 
 */
package io.enoy.tbc.application.control.concurrent;

import javafx.beans.property.ObjectProperty;
import io.enoy.tbc.application.control.concurrent.UserHandler.CommandTask;
import io.enoy.tbc.application.view.TelegramBotTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Enis.Oezsoy
 */
@Component
public class CommandExecuter
{

	@Autowired(required = false)
	private TaskHandler taskHandler;

	private ExecutorService pool;
	
	private volatile int runningCommands = 0;

	@PostConstruct
	public void init()
	{
		pool = Executors.newFixedThreadPool(8, r -> {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			return thread;
		});
	}

	public synchronized void submit(CommandTask commandTask)
	{
		if (taskHandler != null)
		{
			ObjectProperty<TelegramBotTask> uiBotTask = taskHandler.handleTask(commandTask);
			uiBotTask.addListener((v, o, n) -> {
				pool.submit(getCountedRunnable(n));
			});
		}
		else
		{
			pool.submit(getCountedRunnable(commandTask));
		}

	}

	private Runnable getCountedRunnable(Runnable commandTask) {
		return ()-> {
			runningCommands++;
			commandTask.run();
			runningCommands--;
		};
	}

	public synchronized void shutdown()
	{
		if (!pool.isShutdown())
		{
			pool.shutdown();
			try
			{
				pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public int getRunningCommands() {
		return runningCommands;
	}

}
