package io.enoy.tbc.application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

public class Command extends TelegramBotConfiguratorObject
{

	private static final long serialVersionUID = -5257135278006616528L;

	private StringProperty command;
	private StringProperty description;
	private StringProperty script;
	private ObservableMap<ArgumentDefinition, Set<Argument>> arguments;

	public Command()
	{
		this.command = new SimpleStringProperty();
		this.description = new SimpleStringProperty();
		this.script = new SimpleStringProperty();
		this.arguments = FXCollections.observableHashMap();
	}

	public ObservableMap<ArgumentDefinition, Set<Argument>> getArguments()
	{
		return arguments;
	}

	public final StringProperty commandProperty()
	{
		return this.command;
	}

	public final String getCommand()
	{
		return this.commandProperty().get();
	}

	public final void setCommand(final String command)
	{
		this.commandProperty().set(command);
	}

	public final StringProperty scriptProperty()
	{
		return this.script;
	}

	public final String getScript()
	{
		return this.scriptProperty().get();
	}

	public final void setScript(final String script)
	{
		this.scriptProperty().set(script);
	}

	public StringProperty descriptionProperty()
	{
		return this.description;
	}

	public String getDescription()
	{
		return this.descriptionProperty().get();
	}

	public void setDescription(final String description)
	{
		this.descriptionProperty().set(description);
	}

	public Set<Argument> getArguments(int position)
	{
		Optional<Entry<ArgumentDefinition, Set<Argument>>> argument = getArguments().entrySet().stream()//
			.filter(e -> e.getKey().getPosition() == position)//
			.findAny();

		if (argument.isPresent())
		{
			return argument.get().getValue();
		}
		return null;
	}

	public ArgumentDefinition getArgumentDefinition(int position)
	{
		Optional<ArgumentDefinition> definition = getArguments().keySet().stream()//
			.filter(def -> def.getPosition() == position)//
			.findAny();

		return definition.orElse(null);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Command)
		{
			return ( (Command) obj ).getCommand().toLowerCase().equals(this.getCommand().toLowerCase());
		}
		return false;
	}

	@Override
	public String toString()
	{
		return getCommand();
	}

	public Optional<Argument> getArgument(int position, String argument)
	{
		return getArguments(position).stream().filter(a -> a.getArgument().equals(argument)).findAny();
	}

}
