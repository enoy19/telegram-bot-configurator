package io.enoy.tbc.application.view;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PropertySheet;
import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import io.enoy.tbc.application.control.enoyfx.RootFxmlLoader;
import io.enoy.tbc.application.control.enoyfx.controlsfx.LocalizedPropertyItem;
import io.enoy.tbc.application.control.ObservableDataContainer;
import io.enoy.tbc.application.model.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class ConfigurationView extends VBox {

	@Autowired
	private ResourcePropertyBundle bundle;

	@Autowired
	private ObservableDataContainer dataContainer;

	@Autowired
	private RootFxmlLoader rootFxmlLoader;

	@FXML
	private PropertySheet properties;

	@PostConstruct
	public void init() {

		rootFxmlLoader.load(this, bundle.getResourceBundle(), "/fxml/TBCConfiguration.fxml");

		dataContainer.getConfig().addListener((v, o, n) -> {
			setupPropertySheet(n);
		});

		setupPropertySheet(dataContainer.getConfig().get());

	}

	private void setupPropertySheet(Configuration n) {
		properties.getItems().setAll(LocalizedPropertyItem.getLocalizedBeanProperties(bundle.getResourceBundle(), n));
	}

}
