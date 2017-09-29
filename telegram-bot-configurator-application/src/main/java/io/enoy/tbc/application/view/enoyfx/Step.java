/**
 * 
 */
package io.enoy.tbc.application.view.enoyfx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

/**
 * @author Enis.Oezsoy
 */
public class Step<T> extends VBox
{

	public static final Insets DEFAULT_PADDING = new Insets(8);

	private ObjectProperty<EventHandler<StepDoneEvent>> done;
	private ObjectProperty<EventHandler<StepFailedEvent>> failed;
	protected ObjectProperty<T> data;
	BooleanProperty showing;
	private boolean setToDone;

	public Step()
	{
		setAlignment(Pos.TOP_CENTER);
		setPadding(DEFAULT_PADDING);
		done = new SimpleObjectProperty<>();
		failed = new SimpleObjectProperty<>();
		data = new SimpleObjectProperty<>();
		showing = new SimpleBooleanProperty();
		showing.addListener((v, o, n) -> {
			if (n && setToDone)
			{
				done();
			}
		});
	}

	protected void done()
	{
		setToDone = true;
		if (showing.get() && done.get() != null)
		{
			done.get().handle(new StepDoneEvent(data.get()));
		}
	}

	protected void done(T data)
	{
		this.data.set(data);
		done();
	}

	protected void failed(Throwable t)
	{
		if (failed.get() != null)
		{
			failed.get().handle(new StepFailedEvent(t));
		}
	}

	public void setOnDone(final EventHandler<StepDoneEvent> done)
	{
		this.done.set(done);
	}

	public void setOnFailed(final EventHandler<StepFailedEvent> failed)
	{
		this.failed.set(failed);
	}

	public void show(Object... data)
	{
		this.setVisible(true);
	}

	public void hide()
	{
		this.setVisible(false);
		showing.set(false);
		setToDone = false;
	}

	public class StepDoneEvent extends Event
	{

		private static final long serialVersionUID = -1649747551501400914L;
		private transient T data;

		public StepDoneEvent(T data)
		{
			super(Step.this, null, EventType.ROOT);
			this.data = data;
		}

		public T getData()
		{
			return data;
		}

	}

	public class StepFailedEvent extends Event
	{
		private static final long serialVersionUID = -239526823740466869L;
		private transient Throwable throwable;

		public StepFailedEvent(Throwable t)
		{
			super(Step.this, null, EventType.ROOT);
			this.throwable = t;
		}

		public Throwable getThrowable()
		{
			return throwable;
		}
	}

}
