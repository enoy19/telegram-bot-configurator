/**
 * 
 */
package io.enoy.tbc.application.view.property.editor;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PropertySheet.Item;
import io.enoy.tbc.application.control.enoyfx.controlsfx.CustomPropertyEditor;
import io.enoy.tbc.application.model.Role;

import java.util.ResourceBundle;

/**
 * @author Enis.Oezsoy
 */
public class RolePropertyEditor extends CustomPropertyEditor<Role>
{

	private static ObservableList<Role> roles;
	private static ResourceBundle bundle;

	public RolePropertyEditor(Item item)
	{
		super(item);
		ComboBox<Role> comboBox = new ComboBox<>(roles);
		comboBox.setOnAction(e -> {
			setValue(comboBox.getSelectionModel().getSelectedItem());
		});
		comboBox.getSelectionModel().select((Role) item.getValue());

		Button clear = new Button(bundle.getString("clear"));
		clear.setOnAction(e -> {
			comboBox.getSelectionModel().clearSelection();
		});

		HBox hBox = new HBox(5, comboBox, clear);

		this.node = hBox;
	}

	public static void initRoles(ObservableList<Role> roles)
	{
		RolePropertyEditor.roles = roles;
	}

	public static void initResourceBundle(ResourceBundle bundle)
	{
		RolePropertyEditor.bundle = bundle;
	}

}
