/**
 * 
 */
package io.enoy.tbc.application.view.enoyfx;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

/**
 * @author Enis.Oezsoy
 */
public class StrechGroup extends Group
{

	private Pane contentPane;
	private DoubleProperty prefWidth;
	private DoubleProperty prefHeight;

	public StrechGroup(double width, double height, Node... content)
	{
		super();
		contentPane = new Pane(content);
		super.getChildren().setAll(contentPane);

		prefWidth = new SimpleDoubleProperty();
		prefHeight = new SimpleDoubleProperty();
		prefWidth.addListener((v, o, n) -> prefWidth(n.doubleValue()));
		prefHeight.addListener((v, o, n) -> prefHeight(n.doubleValue()));
		prefWidth.set(width);
		prefHeight.set(height);
		sceneProperty().addListener(this::sceneChanged);
	}

	public Pane getContentPane()
	{
		return contentPane;
	}

	public void sceneChanged(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue)
	{
		Scale scale = new Scale();
		scale.xProperty().bind(getScene().widthProperty().divide(prefWidth));
		scale.yProperty().bind(getScene().heightProperty().divide(prefHeight));
		scale.setPivotX(0);
		scale.setPivotY(0);
		this.getTransforms().addAll(scale);
	}

	public DoubleProperty prefWidthProperty()
	{
		return this.prefWidth;
	}

	public double getPrefWidth()
	{
		return this.prefWidthProperty().get();
	}

	public void setPrefWidth(final double prefWidth)
	{
		this.prefWidthProperty().set(prefWidth);
	}

	public DoubleProperty prefHeightProperty()
	{
		return this.prefHeight;
	}

	public double getPrefHeight()
	{
		return this.prefHeightProperty().get();
	}

	public void setPrefHeight(final double prefHeight)
	{
		this.prefHeightProperty().set(prefHeight);
	}

	@Override
	public ObservableList<Node> getChildren()
	{
		return super.getChildrenUnmodifiable();
	}

}
