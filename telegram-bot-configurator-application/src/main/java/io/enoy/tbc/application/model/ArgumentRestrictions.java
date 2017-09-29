package io.enoy.tbc.application.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public class ArgumentRestrictions extends TelegramBotConfiguratorObject
{

	private static final long serialVersionUID = 1015287340930857754L;

	private ObservableSet<String> restrictions;

	public ArgumentRestrictions()
	{
		restrictions = FXCollections.observableSet();
	}

	public ObservableSet<String> getRestrictions()
	{
		return restrictions;
	}

}
