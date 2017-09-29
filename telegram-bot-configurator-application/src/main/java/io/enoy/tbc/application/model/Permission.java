package io.enoy.tbc.application.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import io.enoy.tbc.application.model.input.UserInput;
import io.enoy.tbc.application.model.input.UserInput.UserInputType;

import java.util.Set;

public class Permission extends TelegramBotConfiguratorObject
{

	private static final long serialVersionUID = -3400551364743143859L;

	private StringProperty name;
	private ObjectProperty<Command> command;
	private ObservableMap<Integer, ArgumentRestrictions> restrictions;

	public Permission()
	{
		this.name = new SimpleStringProperty();
		this.command = new SimpleObjectProperty<>();
		this.restrictions = FXCollections.observableHashMap();
	}

	public ObservableMap<Integer, ArgumentRestrictions> getRestrictions()
	{
		return restrictions;
	}

	public ArgumentRestrictions getRestriction(int position)
	{
		return getRestrictions().get(position);
	}

	public boolean isArgumentAllowed(UserInput userInput, int position)
	{
		// TODO: %PHOTO%, %VIDEO% arguments
		Set<Argument> definedArguments = command.get().getArguments(position);
		ArgumentRestrictions restrictions = getRestriction(position);

		if (restrictions == null && definedArguments == null)
		{
			return true;
		}

		if (userInput.getType() == UserInputType.TEXT)
		{
			String text = (String) userInput.getData();

			if (restrictions == null && definedArguments != null)
			{
				return isTextAllowedDefined(definedArguments, text);
			}

			if (definedArguments == null && restrictions != null)
			{
				return isTextAllowedDefined(restrictions, text);
			}

			if (definedArguments != null && restrictions != null)
			{
				return isTextAllowedDefined(definedArguments, text) && isTextAllowedDefined(restrictions, text);
			}

		}

		return false;
	}

	private boolean isTextAllowedDefined(ArgumentRestrictions restrictions, String text)
	{
		return restrictions.getRestrictions().contains(text);
	}

	private boolean isTextAllowedDefined(Set<Argument> definedArguments, String text)
	{
		return definedArguments.stream().filter(a -> a.getArgument().equals(text)).findAny().isPresent();
	}

	// TODO: jfx methods remove final

	public final StringProperty nameProperty()
	{
		return this.name;
	}

	public final String getName()
	{
		return this.nameProperty().get();
	}

	public final void setName(final String name)
	{
		this.nameProperty().set(name);
	}

	public final ObjectProperty<Command> commandProperty()
	{
		return this.command;
	}

	public final Command getCommand()
	{
		return this.commandProperty().get();
	}

	public final void setCommand(final Command command)
	{
		this.commandProperty().set(command);
	}

	@Override
	public String toString()
	{
		return getName() + " (/" + getCommand().getCommand() + ")";
	}

}
