/**
 * 
 */
package io.enoy.tbc.application.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ListSelectionView;
import io.enoy.tbc.application.control.enoyfx.ControlUtils;
import io.enoy.tbc.application.control.enoyfx.ResourcePropertyBundle;
import io.enoy.tbc.application.control.enoyfx.RootFxmlLoader;
import io.enoy.tbc.application.control.ObservableDataContainer;
import io.enoy.tbc.application.control.exceptions.RoleExistsException;
import io.enoy.tbc.application.model.Permission;
import io.enoy.tbc.application.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * @author Enis.Oezsoy
 */
@Component
public class RolesView extends SplitPane implements EventHandler<Event>
{

	@Autowired
	private ResourcePropertyBundle bundle;

	@FXML
	private ListView<Role> listViewRoles;

	@FXML
	private TextField textFieldName;

	@FXML
	private Button buttonAddRole;

	@FXML
	private ListSelectionView<Permission> listSelectionViewPermissions;

	@FXML
	private ComboBox<Role> comboBoxParent;

	@FXML
	private VBox details;

	@Autowired
	private ObservableDataContainer dataContainer;

	@Autowired
	private RootFxmlLoader rootFxmlLoader;

	private ObservableList<Role> roles;
	private ObservableList<Permission> permissions;

	private ReadOnlyObjectProperty<Role> selectedRole;

	private ListChangeListener<Permission> permissionListChangeListener;

	@PostConstruct
	public void init()
	{
		rootFxmlLoader.load(this, bundle.getResourceBundle(), "/fxml/TBCRoles.fxml");

		roles = dataContainer.getRoles();
		permissions = dataContainer.getPermissions();

		buttonAddRole.disableProperty()//
			.bind(Bindings.createBooleanBinding(() -> {
				return textFieldName.getText().trim().isEmpty();
			}, textFieldName.textProperty()));
		buttonAddRole.setOnAction(e -> addRole());
		textFieldName.setOnAction(e -> addRole());

		selectedRole = listViewRoles.getSelectionModel().selectedItemProperty();
		selectedRole.addListener((v, o, n) -> {
			setupRole(n);
		});

		// listViewRoles.setItems(roles);

		comboBoxParent.setOnAction(e -> {
			Role selected = selectedRole.get();

			if (selected != null)
			{
				selected.setParent(comboBoxParent.getSelectionModel().getSelectedItem());
			}
		});

		setupRole(null);

		ControlUtils.addDeleteMenuItemToContextMenu(listViewRoles).textProperty().bind(bundle.get("delete"));

	}

	private void setupRole(Role n)
	{
		if (n != null)
		{
			Role parent = n.getParent();

			resetRoleDetails();

			comboBoxParent.setItems(roles.filtered(r -> {
				if (n.equals(r))
				{
					return false;
				}
				else if (n.isAncestorOf(r))
				{
					return false;
				}
				return true;
			}));
			comboBoxParent.getSelectionModel().select(parent);

			listSelectionViewPermissions.getSourceItems().removeAll(n.getPermissions());
			listSelectionViewPermissions.getTargetItems().setAll(n.getPermissions());

			permissionListChangeListener = c -> {
				while (c.next())
				{
					// Nothing
				}
				if (n != null)
				{
					n.getPermissions().clear();
					n.getPermissions().addAll(c.getList());
				}
			};

			listSelectionViewPermissions.getTargetItems().addListener(permissionListChangeListener);

			details.setDisable(false);
		}
		else
		{
			details.setDisable(true);
		}

	}

	private void resetRoleDetails()
	{
		comboBoxParent.setItems(null);
		if (permissionListChangeListener != null)
		{
			listSelectionViewPermissions.getTargetItems().removeListener(permissionListChangeListener);
		}
		listSelectionViewPermissions.getTargetItems().clear();
		listSelectionViewPermissions.getSourceItems().setAll(permissions);
	}

	private void addRole()
	{
		if (buttonAddRole.isDisabled())
			return;

		String name = textFieldName.getText();

		try
		{
			addRole(name);
			textFieldName.requestFocus();
			textFieldName.selectAll();
		}
		catch (InvalidParameterException | RoleExistsException e)
		{
			e.printStackTrace();
			// TODO: dialog
		}
	}

	public void addRole(String name) throws InvalidParameterException, RoleExistsException
	{

		name = name.trim();

		if (name.isEmpty())
		{
			throw new InvalidParameterException("name must not be empty");
		}

		Role role = new Role();
		role.setName(name);

		if (roles.contains(role))
		{
			throw new RoleExistsException();
		}

		roles.add(role);

	}

	@FXML
	private void clearParent()
	{
		comboBoxParent.getSelectionModel().select(null);
	}

	@Override
	public void handle(Event event)
	{
		// Tab Selection Changed
		Tab tab = (Tab) event.getSource();
		if (!tab.isSelected())
		{
			listViewRoles.getSelectionModel().clearSelection();
			listViewRoles.setItems(null);
		}else{
			listViewRoles.setItems(roles);
		}
	}

}
