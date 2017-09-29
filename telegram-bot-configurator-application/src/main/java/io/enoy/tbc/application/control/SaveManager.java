/**
 * 
 */
package io.enoy.tbc.application.control;

import io.enoy.tbc.commons.TBCRuntime;
import javafx.application.Platform;
import io.enoy.tbc.application.model.DataContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @author Enis.Oezsoy
 */
@Component
public class SaveManager
{

	@Autowired
	private ObservableDataContainer dataContainer;

	@Autowired
	private TBCRuntime tbcRuntime;

	private File currentFile;

	public synchronized void clear()
	{
		currentFile = null;
		dataContainer.reset();
	}

	public synchronized boolean open(File file)
	{
		currentFile = file;
		return load(file);
	}

	public synchronized boolean load(File file) {
		DataContainer openDataContainer;

		synchronized (dataContainer)
		{
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
			{
				openDataContainer = (DataContainer) ois.readObject();
				if(tbcRuntime.isFx()){
					Platform.runLater(()->{
						synchronized (dataContainer)
						{
							dataContainer.setAll(openDataContainer);
						}
					});
				}else{
					dataContainer.setAll(openDataContainer);
				}
			}
			catch (ClassNotFoundException | IOException e)
			{
				e.printStackTrace();
				currentFile = null;
				return false;
			}
		}
		return true;
	}

	public synchronized boolean save()
	{
		if (!hasCurrentFile())
			throw new NullPointerException("currentFile is null");
		return save(currentFile);
	}

	public synchronized boolean save(File file)
	{

		currentFile = file;
		synchronized (dataContainer)
		{
			DataContainer saveDataContainer = new DataContainer(//
				dataContainer.getConfig().get(), //
				dataContainer.getUsers(), //
				dataContainer.getPermissions(), //
				dataContainer.getRoles(), //
				dataContainer.getCommands());

			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file)))
			{
				oos.writeObject(saveDataContainer);
				oos.flush();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				currentFile = null;
				return false;
			}
		}
		return true;

	}

	public File getCurrentFile() {
		return currentFile;
	}
	
	public boolean hasCurrentFile()
	{
		return currentFile != null;
	}

}
