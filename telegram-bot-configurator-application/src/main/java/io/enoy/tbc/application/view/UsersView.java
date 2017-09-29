/**
 * 
 */
package io.enoy.tbc.application.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.controlsfx.control.ListSelectionView;
import io.enoy.tbc.application.control.enoyfx.ControlUtils;
import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import io.enoy.tbc.application.control.enoyfx.RootFxmlLoader;
import io.enoy.tbc.application.view.enoyfx.StringMapTableView;
import io.enoy.tbc.application.control.ObservableDataContainer;
import io.enoy.tbc.application.control.exceptions.UserExistsException;
import io.enoy.tbc.application.model.Permission;
import io.enoy.tbc.application.model.Role;
import io.enoy.tbc.application.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Enis.Oezsoy
 */
@Component
public class UsersView extends SplitPane implements EventHandler<Event>, Initializable
{

	@Autowired
	private ResourcePropertyBundle bundle;

	@FXML
	private TableView<User> tableViewUsers;

	@FXML
	private TableColumn<User, Boolean> tableColumnActivated;

	@FXML
	private TableColumn<User, String> tableColumnId;

	@FXML
	private TableColumn<User, String> tableColumnName;

	@FXML
	private TextField textFieldId;

	@FXML
	private TextField textFieldName;

	@FXML
	private Button buttonAddUser;

	@FXML
	private ListSelectionView<Role> listSelectionViewRoles;

	@FXML
	private ListSelectionView<Permission> listSelectionViewPermissions;

	@FXML
	private Button buttonAddUserProperty;

	@FXML
	private Accordion accordion;

	@FXML
	private StringMapTableView userProperties;

	@Autowired
	private ObservableDataContainer dataContainer;

	@Autowired
	private RootFxmlLoader rootFxmlLoader;

	private ObservableList<User> users;
	private ObservableList<Role> roles;
	private ObservableList<Permission> permissions;

	private ReadOnlyObjectProperty<User> selectedUser;

	private MapChangeListener<String, String> mapChangeListener;

	private ListChangeListener<? super Role> roleListChangeListener;
	private ListChangeListener<? super Permission> permissionListChangeListener;

	@PostConstruct
	public void init()
	{
		rootFxmlLoader.load(this, bundle.getResourceBundle(), "/fxml/TBCUsers.fxml");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		tableColumnActivated.setCellValueFactory(new PropertyValueFactory<>("activated"));
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		tableColumnActivated.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnActivated));
		tableColumnName.setCellFactory(TextFieldTableCell.forTableColumn());

		users = dataContainer.getUsers();
		roles = dataContainer.getRoles();
		permissions = dataContainer.getPermissions();

		// tableViewUsers.setItems(users);
		selectedUser = tableViewUsers.getSelectionModel().selectedItemProperty();

		selectedUser.addListener((v, o, n) -> setupUser(n, o));

		buttonAddUser.disableProperty()//
				.bind(Bindings.createBooleanBinding(() -> {
					return textFieldId.getText().trim().isEmpty();
				}, textFieldId.textProperty()));

		buttonAddUser.setOnAction(e -> addUser());

		textFieldId.setOnAction(e -> textFieldName.requestFocus());
		textFieldName.setOnAction(e -> addUser());

		setupUser(null, null);

		ControlUtils.addDeleteMenuItemToContextMenu(tableViewUsers).textProperty().bind(bundle.get("delete"));
	}

	private void setupUser(User user, User oldUser)
	{
		resetUserDetails(oldUser);

		if (user != null)
		{
			listSelectionViewRoles.getSourceItems().removeAll(user.getRoles());
			// user.getRoles().forEach(r -> {
			// listSelectionViewRoles.getSourceItems().remove(r);
			// });
			listSelectionViewPermissions.getSourceItems().removeAll(user.getPermissions());

			listSelectionViewRoles.getTargetItems().setAll(user.getRoles());
			listSelectionViewPermissions.getTargetItems().setAll(user.getPermissions());

			mapChangeListener = c -> {
				updatePropertiesTable(user);
			};

			roleListChangeListener = c -> {
				user.getRoles().clear();
				user.getRoles().addAll(c.getList());
			};

			permissionListChangeListener = c -> {
				user.getPermissions().clear();
				user.getPermissions().addAll(c.getList());
			};

			user.getProperties().addListener(mapChangeListener);
			listSelectionViewRoles.getTargetItems().addListener(roleListChangeListener);
			listSelectionViewPermissions.getTargetItems().addListener(permissionListChangeListener);

			accordion.setDisable(false);

			updatePropertiesTable(user);
		}
		else
		{
			accordion.setDisable(true);
		}
	}

	private void updatePropertiesTable(User user)
	{
		userProperties.getMapTable().setMap(user.getProperties());
	}

	private void resetUserDetails()
	{
		listSelectionViewRoles.getTargetItems().clear();
		listSelectionViewPermissions.getTargetItems().clear();

		listSelectionViewRoles.getSourceItems().setAll(roles);
		listSelectionViewPermissions.getSourceItems().setAll(permissions);

		userProperties.getMapTable().setMap(null);

	}

	private void resetUserDetails(User oldUser)
	{
		if (oldUser != null && mapChangeListener != null)
		{
			if (mapChangeListener != null)
			{
				oldUser.getProperties().removeListener(mapChangeListener);
			}
			if (roleListChangeListener != null)
			{
				listSelectionViewRoles.getTargetItems().removeListener(roleListChangeListener);
			}
			if (permissionListChangeListener != null)
			{
				listSelectionViewPermissions.getTargetItems().removeListener(permissionListChangeListener);
			}
		}

		resetUserDetails();

	}

	private void addUser()
	{
		if (buttonAddUser.isDisabled())
			return;

		String id = textFieldId.getText();
		String name = textFieldName.getText();

		try
		{
			addUser(id, name);
		}
		catch (InvalidParameterException | UserExistsException e)
		{
			e.printStackTrace();
			// TODO: dialog
		}
	}

	public void addUser(String id, String name) throws InvalidParameterException, UserExistsException
	{

		id = id.trim();

		if (id.isEmpty())
		{
			throw new InvalidParameterException("id must not be empty");
		}

		User user = new User();
		user.setActivated(true);
		user.setId(id);
		user.setName(name);

		if (users.contains(user))
		{
			throw new UserExistsException();
		}

		users.add(user);

	}

	public void addUserProperty(User user, String key, String value)
	{
		if (key != null && !key.trim().isEmpty())
		{
			Map<String, String> properties = user.getProperties();
			properties.put(key, value);
			updatePropertiesTable(user);
		}
	}

	@Override
	public void handle(Event event)
	{
		// Tab Selection Changed
		Tab tab = (Tab) event.getSource();
		if (!tab.isSelected())
		{
			tableViewUsers.getSelectionModel().clearSelection();
			tableViewUsers.setItems(null);
		}else{
			tableViewUsers.setItems(users);
		}
	}

}
