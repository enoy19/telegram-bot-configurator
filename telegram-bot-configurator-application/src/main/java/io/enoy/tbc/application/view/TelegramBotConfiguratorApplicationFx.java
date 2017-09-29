package io.enoy.tbc.application.view;

import com.gluonhq.ignite.spring.SpringContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import io.enoy.tbc.application.control.SaveManager;
import io.enoy.tbc.application.control.concurrent.CommandExecuter;
import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;

public class TelegramBotConfiguratorApplicationFx extends Application {

	private static TelegramBotConfiguratorApplicationFx instance;
	private File parameterFile;
	private Stage primaryStage;

	public TelegramBotConfiguratorApplicationFx() {
		if(instance != null)
			throw new UnsupportedOperationException("There is not enough Heapspace for two of us!");

		instance = this;
	}

	private SpringContext context = new SpringContext(this, () -> Arrays.asList(
			"io.enoy.tbc.application.control",
			"io.enoy.tbc.application.model",
			"io.enoy.tbc.application.view"));

	@Override
	public void start(Stage primaryStage) {
		parameterFile = getFileParameter();
		this.primaryStage = primaryStage;
		context.init();
	}

	private File getFileParameter() {
		Parameters parameters = getParameters();
		if (parameters != null) {
			String filePath = parameters.getNamed().get("--file");
			if (filePath == null) {
				int fileParamIndex = parameters.getRaw().indexOf("--file");
				fileParamIndex++;
				if (parameters.getRaw().size() - 1 >= fileParamIndex) {
					filePath = parameters.getRaw().get(fileParamIndex);
				}
			}
			if (filePath != null) {
				File file = new File(filePath);
				if (file.exists()) {
					return file;
				}
			}
		}
		return null;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public File getParameterFile() {
		return parameterFile;
	}

	public static TelegramBotConfiguratorApplicationFx getInstance() {
		return instance;
	}
}
