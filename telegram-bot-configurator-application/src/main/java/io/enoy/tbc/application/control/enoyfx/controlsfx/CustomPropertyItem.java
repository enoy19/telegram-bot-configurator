/**
 * 
 */
package io.enoy.tbc.application.control.enoyfx.controlsfx;

import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.PropertyEditor;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @author Enis.Oezsoy
 */
public class CustomPropertyItem implements Item
{

	private Item item;
	private Optional<Class<? extends PropertyEditor<?>>> optPropEditor = Optional.empty();

	public CustomPropertyItem(Item item, Class<?> clazz)
	{
		this.item = item;

		try
		{
			Field field = clazz.getDeclaredField(item.getName());
			CustomPropertyEditorClass cpe = field.getDeclaredAnnotation(CustomPropertyEditorClass.class);
			if (cpe != null)
			{
				optPropEditor = Optional.of(cpe.value());
			}
		}
		catch (NoSuchFieldException | SecurityException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public Class<?> getType()
	{
		return item.getType();
	}

	@Override
	public String getCategory()
	{
		return item.getCategory();
	}

	@Override
	public String getName()
	{
		return item.getName();
	}

	@Override
	public String getDescription()
	{
		return item.getDescription();
	}

	@Override
	public Object getValue()
	{
		return item.getValue();
	}

	@Override
	public void setValue(Object value)
	{
		item.setValue(value);
	}

	@Override
	public Optional<ObservableValue<? extends Object>> getObservableValue()
	{
		return item.getObservableValue();
	}

	@Override
	public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass()
	{
		return optPropEditor;
	}

}
