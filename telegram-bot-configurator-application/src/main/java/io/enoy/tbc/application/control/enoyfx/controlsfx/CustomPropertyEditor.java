/**
 * 
 */
package io.enoy.tbc.application.control.enoyfx.controlsfx;

import javafx.scene.Node;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.PropertyEditor;

/**
 * @author Enis.Oezsoy
 */
public class CustomPropertyEditor<T> implements PropertyEditor<T>
{

	protected Item item;
	protected Node node;

	public CustomPropertyEditor(Item item)
	{
		this.item = item;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getValue()
	{
		return (T) item.getValue();
	}

	@Override
	public void setValue(T value)
	{
		item.setValue(value);
	}

	@Override
	public Node getEditor()
	{
		return node;
	}

}
