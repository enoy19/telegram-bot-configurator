/**
 * 
 */
package io.enoy.tbc.application.control;

/**
 * @author Enis.Oezsoy
 */
public interface UiControlAdapter
{

	public void title(String title);

	public void message(String message);

	public void progress(double workDone, double max);

	public void progress(long workDone, long max);

}
