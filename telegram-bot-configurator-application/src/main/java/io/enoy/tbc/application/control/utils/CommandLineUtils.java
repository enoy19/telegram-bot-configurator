/**
 * 
 */
package io.enoy.tbc.application.control.utils;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Enis.Oezsoy
 */
@Component
public class CommandLineUtils
{

	public Process startProcess(String command) throws IOException
	{
		String[] cmdarray = null;
		if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX)
		{
			command = command.replaceAll("'", "'\"\\'\"'");
			cmdarray = new String[] {"/bin/bash", "-c", command};
		}
		else if (SystemUtils.IS_OS_WINDOWS)
		{
			cmdarray = new String[] {"cmd", "/c", '"' + command + '"'};
		}
		Process process = Runtime.getRuntime().exec(cmdarray);
		return process;
	}

	public Object[] exec(String command) throws IOException, InterruptedException
	{
		Process proc = startProcess(command);

		StringBuilder stdOut = new StringBuilder();
		StringBuilder stdErr = new StringBuilder();

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		String s = null;
		while (( s = stdInput.readLine() ) != null)
		{
			stdOut.append(s);
			stdOut.append('\n');
		}

		while (( s = stdError.readLine() ) != null)
		{
			stdErr.append(s);
			stdErr.append('\n');
		}

		proc.waitFor();

		return new Object[] {stdOut.toString(), stdErr.toString(), proc.exitValue()};
	}

}
