/**
 * 
 */
package io.enoy.tbc.application.view;

import io.enoy.tbc.application.control.ObservableDataContainer;
import io.enoy.tbc.application.control.enoyfx.ControlUtils;
import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import io.enoy.tbc.application.control.enoyfx.RootFxmlLoader;
import io.enoy.tbc.application.control.utils.HelpUtils;
import io.enoy.tbc.application.control.utils.KeyboardUtils;
import io.enoy.tbc.application.model.Argument;
import io.enoy.tbc.application.model.ArgumentDefinition;
import io.enoy.tbc.application.model.Command;
import io.enoy.tbc.application.view.enoyfx.StringMapTableView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Enis.Oezsoy
 */
@Component
public class CommandsView extends SplitPane implements EventHandler<Event>, Callback<Collection<Argument>, Void>, Initializable {

	@Autowired
	private ResourcePropertyBundle bundle;

	@Autowired
	private ObservableDataContainer dataContainer;

	@Autowired
	private PermissionsView permissionView;

	@Autowired
	private HelpUtils helpUtils;

	@Autowired
	private RootFxmlLoader rootFxmlLoader;

	@FXML
	private ListView<Command> listViewCommands;

	@FXML
	private TextField textFieldCommand;

	@FXML
	private Button buttonAddCommand;

	@FXML
	private TextField textFieldCommandDescription;

	@FXML
	private Accordion details;

	@FXML
	private TableView<ArgumentDefinition> tableViewDefinitions;

	@FXML
	private TableColumn<ArgumentDefinition, Integer> tableColumnPosition;

	@FXML
	private TableColumn<ArgumentDefinition, String> tableColumnDefinition;

	@FXML
	private TextField textfieldAddArgument;

	@FXML
	private Button buttonAddArgument;

	@FXML
	private StringMapTableView stringMapTableView;

	@FXML
	private TextArea textAreaScript;

	@FXML
	private VBox vBoxArguments;

	@FXML
	private VBox vBoxCommandDetails;

	@FXML
	private ListView<Argument> listViewArguments;

	private ObservableList<Command> commands;
	// private ObservableList<Permission> permissions;

	private ReadOnlyObjectProperty<Command> selectedCommand;
	private ReadOnlyObjectProperty<ArgumentDefinition> selectedDefinition;
	private ReadOnlyObjectProperty<Argument> selectedArgument;

	@PostConstruct
	public void init() {
		rootFxmlLoader.load(this, bundle.getResourceBundle(), "/fxml/TBCCommands.fxml");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		commands = dataContainer.getCommands();

		// listViewCommands.setItems(commands);

		selectedCommand = listViewCommands.getSelectionModel().selectedItemProperty();
		selectedDefinition = tableViewDefinitions.getSelectionModel().selectedItemProperty();
		selectedArgument = listViewArguments.getSelectionModel().selectedItemProperty();

		selectedCommand.addListener((v, o, n) -> {
			setupCommand(n, o);
		});

		selectedDefinition.addListener((v, o, n) -> {
			updateArguments();
		});

		selectedArgument.addListener((v, o, n) -> {
			updateArgument();
		});

		textFieldCommand.setOnAction(e -> addCommand());
		buttonAddCommand.setOnAction(e -> addCommand());

		textfieldAddArgument.setOnAction(e -> addArgument());
		buttonAddArgument.setOnAction(e -> addArgument());

		tableColumnPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
		tableColumnDefinition.setCellValueFactory(new PropertyValueFactory<>("name"));

		tableColumnDefinition.setCellFactory(TextFieldTableCell.forTableColumn());

		// TODO: command list view editable

		ControlUtils.addDeleteMenuItemToContextMenu(listViewCommands).textProperty().bind(bundle.get("delete"));
		ControlUtils.addDeleteMenuItemToContextMenu(listViewArguments, this).textProperty().bind(bundle.get("delete"));

	}

	// private void setupArgument(ArgumentDefinition definition)
	// {
	// if (definition != null)
	// {
	// Command command = selectedCommand.get();
	//
	// Set<Argument> arguments = command.getArguments().get(definition);
	// listViewArguments.getItems().setAll(arguments);
	// }
	// }

	private void updateArguments() {
		// Never use sets again...
		if (selectedDefinition.get() != null) {
			listViewArguments.getItems().setAll(FXCollections.observableArrayList(getSelectedArguments()).sorted());
			vBoxArguments.setDisable(false);
		} else {
			listViewArguments.getItems().clear();
			vBoxArguments.setDisable(true);
		}
	}

	private void updateArgument() {
		if (selectedArgument.get() != null) {
			Argument argument = selectedArgument.get();
			stringMapTableView.getMapTable().setMap(argument.getValueMapping());
			stringMapTableView.setDisable(false);
		} else {
			stringMapTableView.getMapTable().setMap(null);
			stringMapTableView.setDisable(true);
		}
	}

	private Set<Argument> getSelectedArguments() {
		if (selectedCommand.get() != null && selectedDefinition.get() != null) {
			return selectedCommand.get().getArguments().get(selectedDefinition.get());
		}
		return null;
	}

	public void setupCommand(Command command, Command oldCommand) {

		if (oldCommand != null) {
			textFieldCommandDescription.textProperty().unbindBidirectional(oldCommand.descriptionProperty());
			textAreaScript.textProperty().unbindBidirectional(oldCommand.scriptProperty());
		}

		if (command != null) {
			textFieldCommandDescription.textProperty().bindBidirectional(command.descriptionProperty());
			textAreaScript.textProperty().bindBidirectional(command.scriptProperty());
			vBoxCommandDetails.setDisable(false);
		} else {
			vBoxCommandDetails.setDisable(true);
		}

		updateDefinitionsTable();

	}

	private void resortDefinitionTable() {
		tableColumnPosition.setSortType(SortType.ASCENDING);
		tableViewDefinitions.getSortOrder().clear();
		tableViewDefinitions.getSortOrder().add(tableColumnPosition);
	}

	private void updateDefinitionsTable() {
		Command command = selectedCommand.get();

		if (command != null) {
			tableViewDefinitions.setItems(FXCollections.observableArrayList(command.getArguments().keySet()));
			resortDefinitionTable();
		} else {
			tableViewDefinitions.setItems(null);
		}
	}

	private void addArgument() {
		String value = textfieldAddArgument.getText();
		addArgument(value);
		textfieldAddArgument.selectAll();
	}

	public void addArgument(String value) {
		if (value != null) {
			value = value.trim();

			if (value.equals(KeyboardUtils.ABORT)) {
				return;
			}

			Set<Argument> arguments = getSelectedArguments();
			if (arguments != null) {
				Argument argument = new Argument();
				argument.setArgument(value);

				if (!arguments.contains(argument)) {
					arguments.add(argument);
					updateArguments();
				} else {
					// TODO: Dialog?
				}
			}
		}

	}

	@FXML
	private void removeLastDefinition() {
		Command command = selectedCommand.get();

		if (command != null) {
			Optional<ArgumentDefinition> optIntPos = getLastDefinition(command);

			if (optIntPos.isPresent()) {
				command.getArguments().remove(optIntPos.get());
				updateDefinitionsTable();
			}
		}
	}

	@FXML
	private void addDefinition() {
		Command command = selectedCommand.get();

		if (command != null) {
			addDefinition(command, "");
		}

	}

	private void addDefinition(Command command, String name) {
		OptionalInt optIntPos = getLastDefinitionPosition(command);

		int nextPos = 1;

		if (optIntPos.isPresent()) {
			nextPos = optIntPos.getAsInt() + 1;
		}

		ArgumentDefinition aDef = new ArgumentDefinition();
		aDef.setName(nextPos + ") " + name);
		aDef.setPosition(nextPos);

		command.getArguments().put(aDef, new HashSet<>());

		updateDefinitionsTable();
	}

	private OptionalInt getLastDefinitionPosition(Command command) {
		OptionalInt optIntPos = command.getArguments().keySet().stream()//
				.mapToInt(def -> def.getPosition()).max();
		return optIntPos;
	}

	private Optional<ArgumentDefinition> getLastDefinition(Command command) {
		IntegerProperty positionMax = new SimpleIntegerProperty(0);
		command.getArguments().keySet().stream()//
				.forEach(ad -> {
					if (ad.getPosition() > positionMax.get()) {
						positionMax.set(ad.getPosition());
					}
				});

		return command.getArguments().keySet().stream()//
				.filter(ad -> ad.getPosition() == positionMax.get())//
				.findFirst();
	}

	public void addCommand() {
		addCommand(textFieldCommand.getText());
		textFieldCommand.selectAll();
	}

	public void addCommand(String name) {

		if (name != null) {
			name = name.trim();

			if (helpUtils.isHelpCommand(name)) {
				return;
			}

			if (!name.isEmpty()) {
				name = name.replaceAll("\\W", "_");
				Command command = new Command();
				command.setCommand(name);
				command.setScript(bundle.get("script.bindings").get());
				if (!commands.contains(command)) {
					commands.add(command);
					permissionView.addPermission(command.getCommand(), command);
				} else {
					// TODO: Dialog?
				}
			}
		}

	}

	@Override
	public void handle(Event event) {
		// Tab Selection Changed
		Tab tab = (Tab) event.getSource();
		if (!tab.isSelected()) {
			listViewCommands.getSelectionModel().clearSelection();
			listViewCommands.setItems(null);
		}else{
			listViewCommands.setItems(commands);
		}
	}

	@Override
	public Void call(Collection<Argument> param) {
		// on arguments removed
		Set<Argument> arguments = getSelectedArguments();
		if (arguments != null) {
			arguments.removeAll(param);
		}

		return null;
	}
}
