/**
 * 
 */
package io.enoy.tbc.application.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Enis.Oezsoy
 */
public class ArgumentDefinition extends TelegramBotConfiguratorObject
{

	private static final long serialVersionUID = 8246701074375921452L;

	private IntegerProperty position;
	private StringProperty name;

	public ArgumentDefinition()
	{
		position = new SimpleIntegerProperty(0);
		name = new SimpleStringProperty("");
	}

	public IntegerProperty positionProperty()
	{
		return this.position;
	}

	public int getPosition()
	{
		return this.positionProperty().get();
	}

	public void setPosition(final int position)
	{
		this.positionProperty().set(position);
	}

	public StringProperty nameProperty()
	{
		return this.name;
	}

	public String getName()
	{
		return this.nameProperty().get();
	}

	public void setName(final String name)
	{
		this.nameProperty().set(name);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ArgumentDefinition)
			return ( (ArgumentDefinition) obj ).getPosition() == getPosition();
		return false;
	}

}
