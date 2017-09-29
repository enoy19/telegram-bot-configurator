/**
 * 
 */
package io.enoy.tbc.application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Enis.Oezsoy
 */
public class DataContainer implements Serializable
{

	private static final long serialVersionUID = -4337308908979529687L;

	private Configuration config;
	private List<User> users;
	private List<Permission> permissions;
	private List<Role> roles;
	private List<Command> commands;

	public DataContainer()
	{
		config = new Configuration();
		users = new ArrayList<>();
		permissions = new ArrayList<>();
		roles = new ArrayList<>();
		commands = new ArrayList<>();
	}

	public DataContainer(Configuration config, Collection<User> users, Collection<Permission> permissions, Collection<Role> roles,
		Collection<Command> commands)
	{
		this.config = config;
		this.users = new ArrayList<>(users);
		this.permissions = new ArrayList<>(permissions);
		this.roles = new ArrayList<>(roles);
		this.commands = new ArrayList<>(commands);
	}

	public List<User> getUsers()
	{
		return users;
	}

	public List<Permission> getPermissions()
	{
		return permissions;
	}

	public List<Role> getRoles()
	{
		return roles;
	}

	public List<Command> getCommands()
	{
		return commands;
	}

	public Configuration getConfig()
	{
		return config;
	}

}
