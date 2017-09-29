package io.enoy.tbc.application.view;

import io.enoy.tbc.application.control.SaveManager;
import io.enoy.tbc.application.control.concurrent.CommandExecuter;
import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class TBCFxSetup {

	@Autowired
	private ResourcePropertyBundle bundle;

	@Autowired
	private TelegramBotConfiguratorView tbc;

	@Autowired
	private RunView runView;

	@Autowired
	private CommandExecuter commandExecuter;

	@Autowired
	private SaveManager saveManager;

	@Autowired
	private TelegramBotConfiguratorApplicationFx applicationFx;

	@PostConstruct
	private void postConstruct() {

		Scene scene = new Scene(tbc);

		Stage primaryStage = applicationFx.getPrimaryStage();

		primaryStage.setTitle(bundle.get("title").get());
		primaryStage.setScene(scene);

		primaryStage.setOnCloseRequest(e -> {
			tbc.setDisable(true);
			runView.stopBot();
			commandExecuter.shutdown();
		});

		primaryStage.show();

		File file = applicationFx.getParameterFile();

		if (file != null) {
			saveManager.open(file);
		}
	}

	@Bean
	TelegramBotConfiguratorApplicationFx applicationFx() {
		return TelegramBotConfiguratorApplicationFx.getInstance();
	}

}
