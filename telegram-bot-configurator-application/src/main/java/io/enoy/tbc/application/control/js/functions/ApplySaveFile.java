package io.enoy.tbc.application.control.js.functions;

import javafx.beans.property.ObjectProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.function.Function;

@Component("applySaveFileFunction")
public class ApplySaveFile implements Function<File, Boolean>{

	@Autowired
	@Qualifier("applySaveFile")
	private ObjectProperty<File> applySaveFile;
	
	public ApplySaveFile() {
		
	}
	
	@Override
	public synchronized Boolean apply(File t) {
		synchronized (applySaveFile) {
			if(applySaveFile.get() == null){
				applySaveFile.set(t);
				return true;
			}else{
				return false;
			}
		}
	}

}
