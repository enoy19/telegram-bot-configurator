/**
 * 
 */
package io.enoy.tbc.application.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import io.enoy.tbc.application.model.*;
import io.enoy.tbc.application.view.property.editor.RolePropertyEditor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

/**
 * @author Enis.Oezsoy
 */
@Component
public class ObservableDataContainer
{

	private ObjectProperty<Configuration> config = new SimpleObjectProperty<Configuration>(new Configuration());
	private ObservableList<User> users = FXCollections.observableArrayList();
	private ObservableList<Role> roles = FXCollections.observableArrayList();
	private ObservableList<Permission> permissions = FXCollections.observableArrayList();
	private ObservableList<Command> commands = FXCollections.observableArrayList();

	@PostConstruct
	private void init()
	{
		// Synchronize!
		users = FXCollections.synchronizedObservableList(users);
		roles = FXCollections.synchronizedObservableList(roles);
		permissions = FXCollections.synchronizedObservableList(permissions);
		commands = FXCollections.synchronizedObservableList(commands);

		roles.addListener((Change<? extends Role> c) -> {
			while (c.next())
			{
				c.getRemoved().forEach(role -> {
					users.stream()//
						.filter(u -> u.getRoles().contains(role))//
						.forEach(u -> u.getRoles().remove(role));

					roles.stream()//
						.filter(r -> {
							if (r.getParent() != null)
								return r.getParent().equals(role);
							return false;
						})//
						.forEach(r -> r.setParent(role.getParent()));
				});
			}
		});

		permissions.addListener((Change<? extends Permission> c) -> {
			while (c.next())
			{
				c.getRemoved().forEach(permission -> {
					roles.stream()//
						.filter(r -> r.getPermissions().contains(permission))//
						.forEach(r -> r.getPermissions().remove(permission));
					users.stream()//
						.filter(u -> u.getPermissions().contains(permission))//
						.forEach(u -> u.getPermissions().remove(permission));
				});
			}
		});

		commands.addListener((Change<? extends Command> c) -> {
			while (c.next())
			{
				c.getRemoved().forEach(command -> {
					permissions.stream()//
						.filter(p -> p.getCommand().equals(command))//
						.collect(Collectors.toList())//
						.forEach(permissions::remove);
					// TODO: when permission command is changable set command null
				});
			}
		});

		RolePropertyEditor.initRoles(roles);
	}

	public ObservableList<User> getUsers()
	{
		return users;
	}

	public ObservableList<Permission> getPermissions()
	{
		return permissions;
	}

	public ObservableList<Role> getRoles()
	{
		return roles;
	}

	public ObservableList<Command> getCommands()
	{
		return commands;
	}

	public ObjectProperty<Configuration> getConfig()
	{
		return config;
	}

	public void reset()
	{
		this.users.clear();
		this.roles.clear();
		this.permissions.clear();
		this.commands.clear();
		this.config.set(new Configuration());
	}

	public void setAll(DataContainer dataContainer)
	{
		config.set(dataContainer.getConfig());
		commands.setAll(dataContainer.getCommands());
		permissions.setAll(dataContainer.getPermissions());
		roles.setAll(dataContainer.getRoles());
		users.setAll(dataContainer.getUsers());
	}

}
