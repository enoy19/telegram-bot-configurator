/**
 * 
 */
package io.enoy.tbc.application.control.utils;

import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import io.enoy.tbc.application.model.ArgumentDefinition;
import io.enoy.tbc.application.model.Command;
import io.enoy.tbc.application.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Enis.Oezsoy
 */
@Component
public class HelpUtils
{

	private static final List<String> HELP = new ArrayList<>(Arrays.asList("help", "?"));
	
	@Autowired
	private ResourcePropertyBundle bundle;

	public String getHelpText(Command command)
	{
		String description = command.getDescription();
		StringBuilder sb =
			new StringBuilder("[" + command.getCommand() + "]: " + ( description != null ? description : "" ) + "\n");

		sb.append(bundle.get("usage").get() + ":\n");
		sb.append("/" + command.getCommand());

		int argumentCount = command.getArguments().size();
		
		//TODO: test
		
		for (int i = 0; i < argumentCount; i++) {
			ArgumentDefinition definition = command.getArgumentDefinition(i+1);
			sb.append(" [" + definition.getName() + "]");
		}

		return sb.toString();
	}

	public String getHelpText(User user, Collection<Command> commands)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(bundle.get("commands").get() + ":\n");
		for (Command command : commands)
		{
			if (user.isPermittedFor(command))
			{
				sb.append(getHelpText(command) + "\n");
			}
		}

		return sb.toString();
	}

	public boolean isHelpCommand(String text)
	{
		final String lowerText = text.trim().toLowerCase();
		return HELP.stream().filter(s -> lowerText.startsWith(s)).findAny().isPresent();
	}

}
