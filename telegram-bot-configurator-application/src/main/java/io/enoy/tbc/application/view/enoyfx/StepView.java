/**
 * 
 */
package io.enoy.tbc.application.view.enoyfx;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.StackPane;

import java.security.InvalidParameterException;

/**
 * @author Enis.Oezsoy
 */
public abstract class StepView extends StackPane
{

	// private FadeTransition fadeIn;
	// private FadeTransition fadeOut;

	private ObjectProperty<Step<?>> previousStep;

	public StepView()
	{
		this(new Step<?>[] {});
	}

	public StepView(Step<?>... steps)
	{
		getStyleClass().add("step-view");

		setSteps(steps);

		previousStep = new SimpleObjectProperty<Step<?>>(null);
		// fadeIn = new FadeTransition(Duration.millis(300));
		// fadeOut = new FadeTransition(Duration.millis(300));
		// fadeIn.setFromValue(0);
		// fadeIn.setToValue(1);
		// fadeOut.setFromValue(1);
		// fadeOut.setToValue(0);
	}

	public void setSteps(Step<?>... steps)
	{
		// remove previous handlers
		resetPreviousChildrenSteps();

		this.getChildren().setAll(steps);

		for (Step<?> step : steps)
		{
			step.hide();
			step.setOnDone(this::onStepDone);
			step.setOnFailed(this::onStepFailed);
		}

		if (steps.length > 0)
		{
			setStep(0);
		}
	}

	private void resetPreviousChildrenSteps()
	{
		this.getChildren().stream()//
			.filter(n -> n instanceof Step<?>)//
			.forEach(n -> {
				( (Step<?>) n ).setOnDone(null);
				( (Step<?>) n ).setOnFailed(null);
			});
	}

	public synchronized void setStep(Step<?> step, Object... data)
	{
		if (getChildren().contains(step))
		{
			hidePreviousStep();

			Platform.runLater(() -> {
				try
				{
					previousStep.set(step);
					step.show(data);
					step.showing.set(true);
				}
				catch (Exception e)
				{
					step.failed(e);
				}
			});
		}
		else
		{
			throw new InvalidParameterException("Step is no child");
		}
	}

	public synchronized void setStep(int index, Object... data)
	{
		if (getChildren().get(index) instanceof Step<?>)
		{
			this.setStep((Step<?>) getChildren().get(index), data);
		}
	}

	public void hidePreviousStep()
	{
		if (previousStep.get() != null)
		{
			previousStep.get().hide();
		}
	}

	// public void hideVisibleSteps()
	// {
	// getChildren().stream()//
	// .filter(n -> n instanceof Step<?> && n.isVisible())//
	// .findAny()//
	// .ifPresent(n -> ( (Step<?>) n ).hide());
	// }

	public abstract void onStepDone(Step<?>.StepDoneEvent e);

	public abstract void onStepFailed(Step<?>.StepFailedEvent e);
}
