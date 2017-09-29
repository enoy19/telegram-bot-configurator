package io.enoy.tbc.application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class Argument extends TelegramBotConfiguratorObject
{

	private static final long serialVersionUID = 894731993630667016L;

	private StringProperty argument;
	private ObservableMap<String, String> valueMapping;

	public Argument()
	{
		this.argument = new SimpleStringProperty();
		this.valueMapping = FXCollections.observableHashMap();
	}

	public ObservableMap<String, String> getValueMapping()
	{
		return valueMapping;
	}

	public String getValue(String key)
	{
		return getValueMapping().get(key);
	}

	public final StringProperty argumentProperty()
	{
		return this.argument;
	}

	public final String getArgument()
	{
		return this.argumentProperty().get();
	}

	public final void setArgument(final String argument)
	{
		this.argumentProperty().set(argument);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Argument)
		{
			return ( (Argument) obj ).getArgument().equals(this.getArgument());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getArgument().hashCode();
	}

	@Override
	public String toString()
	{
		return getArgument();
	}

}
