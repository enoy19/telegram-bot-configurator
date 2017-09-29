/**
 * 
 */
package io.enoy.tbc.application.control.utils;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

/**
 * @author Enis.Oezsoy
 */
public class KeyboardUtils
{

	public static final String ABORT = "🚫";

	public static ReplyKeyboardMarkup getKeyboard(String... keys)
	{
		String[][] keys2d = oneToTwoDimensionalArray(keys, 2);
		String[][] keys2dCancel = new String[keys2d.length + 1][];
		for (int i = 0; i < keys2d.length; i++)
		{
			keys2dCancel[i] = keys2d[i];
		}
		keys2dCancel[keys2d.length] = new String[] {ABORT};

		ReplyKeyboardMarkup keyBoard = new ReplyKeyboardMarkup(keys2dCancel, true, true, true);

		return keyBoard;
	}

	private static String[][] oneToTwoDimensionalArray(String[] array, int columns)
	{
		String[][] result = null;

		int rows = (int) Math.ceil((double) array.length / columns);
		result = new String[rows][];

		for (int i = 0; i < array.length; i += columns)
		{
			int rowIndex = Math.floorDiv(i, columns);
			int remaining = columns;
			if (i + columns > array.length)
			{
				remaining = ( array.length - i ) % columns;
			}
			String[] column = new String[remaining];
			result[rowIndex] = column;
			for (int j = 0; j < column.length; j++)
			{
				column[j] = array[i + j];
			}
		}

		return result;
	}

}
