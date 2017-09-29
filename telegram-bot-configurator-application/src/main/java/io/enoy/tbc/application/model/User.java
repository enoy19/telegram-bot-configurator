package io.enoy.tbc.application.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.util.Set;

public class User extends PermissionContainer
{

	private static final long serialVersionUID = 9035448219790748876L;

	private BooleanProperty activated;
	private StringProperty id;
	private StringProperty name;
	private ObservableSet<Role> roles;
	private ObservableMap<String, String> properties;

	public User()
	{
		roles = FXCollections.observableSet();
		properties = FXCollections.observableHashMap();
		activated = new SimpleBooleanProperty(true);
		id = new SimpleStringProperty();
		name = new SimpleStringProperty();
	}

	public ObservableSet<Role> getRoles()
	{
		return roles;
	}

	public final BooleanProperty activatedProperty()
	{
		return this.activated;
	}

	public final boolean isActivated()
	{
		return this.activatedProperty().get();
	}

	public final void setActivated(final boolean activated)
	{
		this.activatedProperty().set(activated);
	}

	public final StringProperty idProperty()
	{
		return this.id;
	}

	public final String getId()
	{
		return this.idProperty().get();
	}

	public final void setId(final String id)
	{
		this.idProperty().set(id);
	}

	public final StringProperty nameProperty()
	{
		return this.name;
	}

	public final String getName()
	{
		return this.nameProperty().get();
	}

	public final void setName(final String name)
	{
		this.nameProperty().set(name);
	}

	public void setProperties(ObservableMap<String, String> properties)
	{
		this.properties = properties;
	}

	public ObservableMap<String, String> getProperties()
	{
		return properties;
	}
	
	public String getProperty(String key){
		return properties.get(key);
	}
	
	public String getProperty(Object key){
		return getProperty(key.toString());
	}
	
	public String putProperty(Object key, Object value){
		return putProperty(key.toString(), value.toString());
	}
	
	public String setProperty(Object key, Object value){
		return setProperty(key.toString(), value.toString());
	}
	
	public String putProperty(String key, String value){
		return properties.put(key, value);
	}
	
	public String setProperty(String key, String value){
		return this.putProperty(key, value);
	}

	@Override
	public boolean isPermittedFor(Command command)
	{
		boolean permitted = super.isPermittedFor(command);
		if (!permitted)
		{
			permitted = getRoles().stream()//
				.filter(r -> r.isPermittedFor(command))//
				.findAny()//
				.isPresent();
		}
		return permitted;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof User)
			return getId().equals(( (User) obj ).getId());

		return false;
	}

	@Override
	public String toString()
	{
		return getName() + ": " + getId() + ( isActivated() ? "" : " (deactivated)" );
	}

	@Override
	public Set<Permission> getPermissionsFor(final Command command)
	{
		Set<Permission> permissions = super.getPermissionsFor(command);
		getRoles().forEach(r -> permissions.addAll(r.getPermissionsFor(command)));
		return permissions;
	}

}
