/**
 * 
 */
package io.enoy.tbc.application.view;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import io.enoy.tbc.application.control.enoyfx.ControlUtils;
import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import io.enoy.tbc.application.control.enoyfx.RootFxmlLoader;
import io.enoy.tbc.application.control.ObservableDataContainer;
import io.enoy.tbc.application.control.utils.KeyboardUtils;
import io.enoy.tbc.application.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * @author Enis.Oezsoy
 */
@Component
public class PermissionsView extends SplitPane implements EventHandler<Event>
{

	@Autowired
	private ResourcePropertyBundle bundle;

	@Autowired
	private ObservableDataContainer dataContainer;

	@Autowired
	private RootFxmlLoader rootFxmlLoader;

	@FXML
	private ListView<Permission> listViewPermissions;

	@FXML
	private TextField textFieldName;

	@FXML
	private ComboBox<Command> comboBoxCommand;

	@FXML
	private VBox details;

	@FXML
	private Label labelCommand;

	@FXML
	private ListView<Integer> listViewPosition;

	@FXML
	private ListView<String> listViewAllowedArguments;

	@FXML
	private TextField textFieldArgument;

	@FXML
	private ListView<Argument> listViewDefinedArguments;

	@FXML
	private VBox vBoxDefinedArguments;

	@FXML
	private VBox vBoxDetails;

	@FXML
	private VBox vBoxArguments;

	@FXML
	private SplitPane splitPaneDetails;

	private ObservableList<Permission> permissions;

	private ReadOnlyObjectProperty<Permission> selectedPermission;
	private ReadOnlyObjectProperty<Integer> selectedPosition;

	@PostConstruct
	public void init()
	{
		rootFxmlLoader.load(this, bundle.getResourceBundle(), "/fxml/TBCPermissions.fxml");

		permissions = dataContainer.getPermissions();

		// listViewPermissions.setItems(permissions);

		selectedPermission = listViewPermissions.getSelectionModel().selectedItemProperty();
		selectedPosition = listViewPosition.getSelectionModel().selectedItemProperty();

		comboBoxCommand.setItems(dataContainer.getCommands());

		textFieldName.setOnAction(e -> addPermission());
		textFieldArgument.setOnAction(e -> addArgument());

		selectedPermission.addListener(i -> updatePermission());
		selectedPosition.addListener(i -> updateArguments());

		listViewDefinedArguments.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		resetDefinedArguments();

		listViewAllowedArguments.getItems().addListener((Change<? extends String> c) -> {
			Integer position = selectedPosition.get();
			Permission permission = selectedPermission.get();
			while (c.next())
			{
				if (position != null && permission != null)
				{
					permission.getRestrictions().get(position).getRestrictions().clear();
					permission.getRestrictions().get(position).getRestrictions().addAll(c.getList());
				}
			}
		});

		ControlUtils.addDeleteMenuItemToContextMenu(listViewPermissions).textProperty().bind(bundle.get("delete"));
		ControlUtils.addDeleteMenuItemToContextMenu(listViewAllowedArguments).textProperty().bind(bundle.get("delete"));

	}

	private void updatePermission()
	{
		Permission permission = selectedPermission.get();
		if (permission != null)
		{
			labelCommand.setText(permission.getCommand().getCommand());
			listViewPosition.getItems().setAll(permission.getRestrictions().keySet());
			vBoxDetails.setDisable(false);
		}
		else
		{
			labelCommand.setText(null);
			listViewPosition.getItems().clear();
			vBoxDetails.setDisable(true);
		}
	}

	private void updateArguments()
	{
		Integer position = selectedPosition.get();
		if (position != null)
		{
			Permission permission = selectedPermission.get();
			listViewAllowedArguments.getItems().setAll(permission.getRestrictions().get(position).getRestrictions());

			Command command = permission.getCommand();
			Optional<ArgumentDefinition> argDef = command.getArguments().keySet().stream()//
				.filter(ad -> ad.getPosition() == position)//
				.findFirst();

			if (argDef.isPresent())
			{
				Set<Argument> definedArguments = command.getArguments().get(argDef.get());
				// TODO: still selected
				if (definedArguments.size() > 0)
				{
					setDefinedArguments(definedArguments);
				}
				else
				{
					resetDefinedArguments();
				}
			}
			else
			{
				resetDefinedArguments();
			}
			vBoxArguments.setDisable(false);
		}
		else
		{
			listViewAllowedArguments.getItems().clear();
			resetDefinedArguments();
			vBoxArguments.setDisable(true);
		}
	}

	private void setDefinedArguments(Set<Argument> definedArguments)
	{
		if (!splitPaneDetails.getItems().contains(vBoxDefinedArguments))
		{
			vBoxDefinedArguments.setDisable(false);
			splitPaneDetails.getItems().add(vBoxDefinedArguments);
		}
		listViewDefinedArguments.getItems().setAll(definedArguments);
	}

	private void resetDefinedArguments()
	{
		if (splitPaneDetails.getItems().contains(vBoxDefinedArguments))
		{
			vBoxDefinedArguments.setDisable(true);
			splitPaneDetails.getItems().remove(vBoxDefinedArguments);
		}
		listViewDefinedArguments.getItems().clear();
	}

	@FXML
	void addPermission()
	{
		addPermission(textFieldName.getText(), comboBoxCommand.getValue());
	}

	public void addPermission(String name, Command command)
	{
		if (name != null && command != null)
		{
			name = name.trim();
			if (!name.isEmpty())
			{
				Permission permission = new Permission();
				permission.setName(name);
				permission.setCommand(command);

				permissions.add(permission);
			}
		}
	}

	@FXML
	void addArgument()
	{
		addArgument(textFieldArgument.getText(), selectedPermission.get(), selectedPosition.get());
		textFieldArgument.selectAll();
	}

	public void addArgument(String argument, Permission permission, int position)
	{
		addArgument(argument, permission, position, false);
	}

	public void addArgument(String argument, Permission permission, int position, boolean toggle)
	{
		if (argument != null && permission != null && position > 0)
		{
			argument = argument.trim();

			if (argument.equals(KeyboardUtils.ABORT))
			{
				return;
			}

			if (!argument.isEmpty())
			{
				ArgumentRestrictions restrictions = permission.getRestrictions().get(position);
				ObservableSet<String> argumentRestrictions = restrictions.getRestrictions();
				boolean newlyAdded = argumentRestrictions.add(argument);
				if (toggle && !newlyAdded)
				{
					argumentRestrictions.remove(argument);
				}
				updateArguments();
			}

		}
	}

	@FXML
	void addRestriction()
	{
		Permission permission = selectedPermission.get();
		if (permission != null)
		{
			ObservableMap<Integer, ArgumentRestrictions> restrictions = permission.getRestrictions();
			OptionalInt max = restrictions.keySet().stream().mapToInt(i -> i).max();

			int nextPosition = 1;

			if (max.isPresent())
			{
				nextPosition = max.getAsInt() + 1;
			}

			ArgumentRestrictions argRestrictions = new ArgumentRestrictions();

			restrictions.put(nextPosition, argRestrictions);

			updatePermission();
		}
	}
	
	@FXML
	void definedArgumentsClick(MouseEvent e){
		if(e.getClickCount() % 2 == 1){
			toggleDefinedArgument();
		}
	}

	@FXML
	void toggleDefinedArgument()
	{
		List<Argument> items = new ArrayList<>(listViewDefinedArguments.getSelectionModel().getSelectedItems());
		items.forEach(argument -> {
			addArgument(argument.getArgument(), selectedPermission.get(), selectedPosition.get(), true);
		});
	}

	@Override
	public void handle(Event event)
	{
		// Tab Selection Changed
		Tab tab = (Tab) event.getSource();
		if (!tab.isSelected())
		{
			listViewPermissions.getSelectionModel().clearSelection();
			listViewPermissions.setItems(null);
		}else{
			listViewPermissions.setItems(permissions);
		}
	}

}
