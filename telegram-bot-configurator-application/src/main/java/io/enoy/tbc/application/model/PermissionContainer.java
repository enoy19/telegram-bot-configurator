package io.enoy.tbc.application.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.Set;
import java.util.stream.Collectors;

public class PermissionContainer extends TelegramBotConfiguratorObject
{

	private static final long serialVersionUID = 562210245487215628L;

	private ObservableSet<Permission> permissions;

	public PermissionContainer()
	{
		permissions = FXCollections.observableSet();
	}

	public ObservableSet<Permission> getPermissions()
	{
		return permissions;
	}

	public boolean isPermittedFor(Command command)
	{
		return getPermissions().stream()//
			.filter(p -> p.getCommand().equals(command)).findAny()//
			.isPresent();
	}

	public Set<Permission> getPermissionsFor(Command command)
	{
		return getPermissions().stream().filter(p -> p.getCommand().equals(command)).collect(Collectors.toSet());
	}

}
