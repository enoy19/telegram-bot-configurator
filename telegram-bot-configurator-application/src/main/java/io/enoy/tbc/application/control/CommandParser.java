/**
 * 
 */
package io.enoy.tbc.application.control;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Enis.Oezsoy
 */
@Component
public class CommandParser
{

	// "((\\"|[^"])*?)"|([^\s]+)
	private static final String REGEX = "\"((\\\\\"|[^\"])*?)\"|([^\\s]+)";
	private static final Pattern PATTERN = Pattern.compile(REGEX);

	public List<String> parseCommand(String message)
	{
		List<String> values = new ArrayList<>();
		Matcher matcher = PATTERN.matcher(message);

		while (matcher.find())
		{
			String value = matcher.group(1);
			if (value == null)
			{
				value = matcher.group(3);
			}
			value = value.replaceAll("\\\\\"", "\"");
			values.add(value);
		}
		return values;
	}

}
