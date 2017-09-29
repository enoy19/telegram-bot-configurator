package io.enoy.tbc.application.control.enoyfx;

import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class RootFxmlLoader
{

	@Autowired
	private FXMLLoader loader;

	public void load(Object root, Object controller, ResourceBundle bundle, InputStream inputStream)
	{
		loader.setRoot(root);
		loader.setController(controller);
		loader.setResources(bundle);
		try {
			loader.load(inputStream);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public void load(Object rootController, ResourceBundle bundle, InputStream inputStream)
	{
		load(rootController, rootController, bundle, inputStream);
	}

	public void load(Object rootController, ResourceBundle bundle, String resourceNameOfRootControllerClass)
	{
		load(rootController, bundle, rootController.getClass().getResourceAsStream(resourceNameOfRootControllerClass));
	}

}
