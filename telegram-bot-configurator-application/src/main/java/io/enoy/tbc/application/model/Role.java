package io.enoy.tbc.application.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Role extends PermissionContainer
{

	private static final long serialVersionUID = 7891910797230133253L;

	private ObjectProperty<Role> parent;
	private StringProperty name;

	public Role()
	{
		this.parent = new SimpleObjectProperty<>();
		this.name = new SimpleStringProperty();
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

	public final ObjectProperty<Role> parentProperty()
	{
		return this.parent;
	}

	public final Role getParent()
	{
		return this.parentProperty().get();
	}

	public final void setParent(final Role parent)
	{
		this.parentProperty().set(parent);
	}

	public boolean isAncestorOf(Role decendent)
	{

		Role parent = decendent.getParent();

		if (parent != null)
		{
			if (parent.equals(this))
			{
				return true;
			}
			else
			{
				return isAncestorOf(parent);
			}
		}

		return false;

	}

	@Override
	public boolean isPermittedFor(Command command)
	{
		boolean permitted = super.isPermittedFor(command);
		if (permitted)
		{
			return true;
		}

		if (!permitted && parent.get() != null)
		{
			return parent.get().isPermittedFor(command);
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Role)
			return ( (Role) obj ).getName().equals(this.getName());
		return false;
	}

	@Override
	public int hashCode()
	{
		return getName().hashCode();
	}

	@Override
	public String toString()
	{
		return getName();
	}

}
