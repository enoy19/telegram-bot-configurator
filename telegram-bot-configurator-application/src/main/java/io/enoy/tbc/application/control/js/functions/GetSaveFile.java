package io.enoy.tbc.application.control.js.functions;

import io.enoy.tbc.application.control.SaveManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.Callable;

@Component
public class GetSaveFile implements Callable<File>{

	@Autowired
	private SaveManager saveManager;
	
	public GetSaveFile() {
		
	}

	@Override
	public File call() throws Exception {
		return saveManager.getCurrentFile();
	}
	
}
