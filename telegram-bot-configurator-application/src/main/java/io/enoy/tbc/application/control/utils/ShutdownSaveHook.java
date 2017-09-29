package io.enoy.tbc.application.control.utils;

import io.enoy.tbc.application.control.ObservableDataContainer;
import io.enoy.tbc.application.control.SaveManager;
import io.enoy.tbc.application.model.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ShutdownSaveHook implements Runnable {

	@Autowired
	private SaveManager saveManager;
	@Autowired
	private ObservableDataContainer dataContainer;

	private Thread shutdownSaveHook;

	@PostConstruct
	private void init() {
		shutdownSaveHook = new Thread(this);
	}

	public void addHook() {
		Runtime.getRuntime().addShutdownHook(shutdownSaveHook);
	}

	public void removeHook() {
		if (!shutdownSaveHook.isAlive())
			Runtime.getRuntime().removeShutdownHook(shutdownSaveHook);
	}

	@Override
	public void run() {
		synchronized (dataContainer) {
			Configuration config = dataContainer.getConfig().get();
			if (config != null && config.isAutoSave()) {
				synchronized (saveManager) {
					saveManager.save();
				}
			}
		}
	}

}
