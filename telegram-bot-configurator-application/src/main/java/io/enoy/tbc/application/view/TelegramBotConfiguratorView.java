package io.enoy.tbc.application.view;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.controlsfx.control.Notifications;
import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import io.enoy.tbc.application.control.enoyfx.RootFxmlLoader;
import io.enoy.tbc.application.control.SaveManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Component
public class TelegramBotConfiguratorView extends VBox
{

	private static final String FILE_EXTENSION = "tbc";

	@Autowired
	private TelegramBotConfiguratorApplicationFx application;

	@Autowired
	private ResourcePropertyBundle bundle;

	@Autowired
	private ConfigurationView configuration;

	@Autowired
	private UsersView users;

	@Autowired
	private RolesView roles;

	@Autowired
	private CommandsView commands;

	@Autowired
	private PermissionsView permissions;

	@Autowired
	private RunView run;

	@Autowired
	private SaveManager saveManager;

	@Autowired
	private RootFxmlLoader rootFxmlLoader;

	@FXML
	private MenuBar menuBar;

	@FXML
	private Tab tabConfiguration;

	@FXML
	private Tab tabUsers;

	@FXML
	private Tab tabPermissions;

	@FXML
	private Tab tabRoles;

	@FXML
	private Tab tabCommands;

	@FXML
	private Tab tabRun;

	private FileChooser fileChooser;

	public TelegramBotConfiguratorView()
	{
	}

	@PostConstruct
	public void init()
	{
		rootFxmlLoader.load(this, bundle.getResourceBundle(), "/fxml/TelegramBotConfigurator.fxml");

		tabConfiguration.setContent(configuration);
		tabUsers.setContent(users);
		tabRoles.setContent(roles);
		tabCommands.setContent(commands);
		tabPermissions.setContent(permissions);
		tabRun.setContent(run);

		tabConfiguration.disableProperty().bind(run.botRunning());
		tabUsers.disableProperty().bind(run.botRunning());
		tabRoles.disableProperty().bind(run.botRunning());
		tabCommands.disableProperty().bind(run.botRunning());
		tabPermissions.disableProperty().bind(run.botRunning());

		menuBar.disableProperty().bind(run.botRunning());

		tabUsers.setOnSelectionChanged(users);
		tabRoles.setOnSelectionChanged(roles);
		tabCommands.setOnSelectionChanged(commands);
		tabPermissions.setOnSelectionChanged(permissions);

		fileChooser = new FileChooser();

		fileChooser.getExtensionFilters()
			.add(new ExtensionFilter(bundle.get("telegram.bot.configurator.file").get(), "*." + FILE_EXTENSION));

		menuBar.getMenus().forEach(m -> bindMenuDisabled(m));

	}

	private void bindMenuDisabled(Menu menu)
	{
		menu.getItems().forEach(m -> {
			m.disableProperty().bind(menuBar.disableProperty());
		});
	}

	@FXML
	public boolean menuNew()
	{
		// TODO: dialog
		// TODO: clear textfields
		saveManager.clear();
		// Dialog return true or false ( for menuOpen )
		return true;
	}

	@FXML
	public void menuOpen()
	{
		File file = fileChooser.showOpenDialog(application.getPrimaryStage());
		if (file != null)
		{
			if (menuNew())
			{
				if (saveManager.open(file))
				{
					infoNotification(bundle.get("opened").get());
				}
				else
				{
					errorNotification(bundle.get("opened.error").get());
				}
			}
		}
	}

	@FXML
	public void menuSave()
	{
		if (!saveManager.hasCurrentFile())
		{
			menuSaveAs();
		}
		else
		{
			if (saveManager.save())
			{
				infoNotification(bundle.get("saved").get());
			}
			else
			{
				errorNotification(bundle.get("saved.error").get());
			}
		}
	}

	@FXML
	public void menuSaveAs()
	{
		File file = fileChooser.showSaveDialog(application.getPrimaryStage());
		if (file != null)
		{
			if (saveManager.save(file))
			{
				infoNotification(bundle.get("saved").get());
			}
			else
			{
				errorNotification(bundle.get("saved.error").get());
			}
		}
	}

	private void infoNotification(String info)
	{
		Notifications.create().owner(this).position(Pos.BOTTOM_RIGHT).text(info).showInformation();
	}

	private void errorNotification(String error)
	{
		Notifications.create().owner(this).position(Pos.BOTTOM_RIGHT).text(error).showError();
	}

}
