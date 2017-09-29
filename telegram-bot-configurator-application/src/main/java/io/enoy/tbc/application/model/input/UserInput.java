/**
 * 
 */
package io.enoy.tbc.application.model.input;

import com.pengrad.telegrambot.model.Message;

/**
 * @author Enis.Oezsoy
 */
public class UserInput
{

	public enum UserInputType
	{
		TEXT, PHOTO, VIDEO, DOCUMENT, AUDIO, STICKER, VOICE, CONTACT, LOCATION;
	}

	private UserInputType type;
	private Object data;
	private String caption;

	private UserInput()
	{

	}

	public UserInputType getType()
	{
		return type;
	}

	public Object getData()
	{
		return data;
	}

	public String getCaption()
	{
		return caption;
	}

	public static UserInput getUserInputFromMessage(Message message)
	{
		UserInput userInput = new UserInput();
		userInput.caption = message.caption();

		if (message.text() != null)
		{
			userInput.type = UserInputType.TEXT;
			userInput.data = message.text();
		}
		else if (message.photo() != null)
		{
			userInput.type = UserInputType.PHOTO;
			userInput.data = message.photo();
		}
		else if (message.video() != null)
		{
			userInput.type = UserInputType.VIDEO;
			userInput.data = message.video();
		}
		else if (message.document() != null)
		{
			userInput.type = UserInputType.DOCUMENT;
			userInput.data = message.document();
		}
		else if (message.audio() != null)
		{
			userInput.type = UserInputType.AUDIO;
			userInput.data = message.audio();
		}
		else if (message.sticker() != null)
		{
			userInput.type = UserInputType.STICKER;
			userInput.data = message.sticker();
		}
		else if (message.voice() != null)
		{
			userInput.type = UserInputType.VOICE;
			userInput.data = message.voice();
		}
		else if (message.contact() != null)
		{
			userInput.type = UserInputType.CONTACT;
			userInput.data = message.contact();
		}
		else if (message.location() != null)
		{
			userInput.type = UserInputType.LOCATION;
			userInput.data = message.location();
		}

		return userInput;
	}

	public static UserInput text(String text)
	{
		UserInput userInput = new UserInput();
		userInput.type = UserInputType.TEXT;
		userInput.data = text;
		return userInput;
	}

	@Override
	public String toString()
	{
		return type + " [" + caption + "]: " + data.toString();
	}

}
