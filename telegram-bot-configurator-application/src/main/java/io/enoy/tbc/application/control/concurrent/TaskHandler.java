/**
 * 
 */
package io.enoy.tbc.application.control.concurrent;

import javafx.beans.property.ObjectProperty;
import io.enoy.tbc.application.control.concurrent.UserHandler.CommandTask;
import io.enoy.tbc.application.view.TelegramBotTask;

/**
 * @author Enis.Oezsoy
 */
public interface TaskHandler
{

	public ObjectProperty<TelegramBotTask> handleTask(CommandTask task);

}
