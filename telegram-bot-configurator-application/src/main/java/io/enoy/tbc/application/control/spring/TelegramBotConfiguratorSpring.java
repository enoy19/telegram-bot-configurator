package io.enoy.tbc.application.control.spring;

import com.pengrad.telegrambot.TelegramBot;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import io.enoy.tbc.application.control.ObservableDataContainer;
import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import io.enoy.tbc.application.model.DataContainer;
import io.enoy.tbc.application.view.property.editor.RolePropertyEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class TelegramBotConfiguratorSpring {

	@Autowired
	private ObservableDataContainer uiDataContainer;

	@Bean
	public ResourcePropertyBundle resourcePropertyBundle() {
		ResourcePropertyBundle rpb = new ResourcePropertyBundle("fxml.bundles.telegram_bot_configurator");
		RolePropertyEditor.initResourceBundle(rpb.getResourceBundle());
		return rpb;
	}

	@Bean
	@Qualifier("dataContainer")
	public ObjectProperty<DataContainer> dataContainer() {
		ObjectProperty<DataContainer> dc = new SimpleObjectProperty<DataContainer>(new DataContainer());
		dc.addListener((v, o, n) -> {
			uiDataContainer.getConfig().set(n.getConfig());
			uiDataContainer.getPermissions().setAll(n.getPermissions());
			uiDataContainer.getRoles().setAll(n.getRoles());
			uiDataContainer.getUsers().setAll(n.getUsers());
		});
		return dc;
	}

	@Bean
	@Qualifier("applySaveFile")
	public ObjectProperty<File> applySaveFile() {
		return new SimpleObjectProperty<>(null);
	}

	@Bean
	@Qualifier("bot")
	public ObjectProperty<TelegramBot> bot() {
		return new SimpleObjectProperty<>(null);
	}

}
