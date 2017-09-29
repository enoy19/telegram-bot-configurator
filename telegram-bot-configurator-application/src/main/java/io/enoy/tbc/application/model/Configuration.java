package io.enoy.tbc.application.model;

import javafx.beans.property.*;
import io.enoy.tbc.application.control.enoyfx.controlsfx.CustomPropertyEditorClass;
import io.enoy.tbc.application.view.property.editor.RolePropertyEditor;

public class Configuration extends TelegramBotConfiguratorObject {

	private static final long serialVersionUID = 795867750235227412L;

	private StringProperty botToken;
	private BooleanProperty addUserToListOnContact;
	private BooleanProperty addUserToListOnContactActivated;
	@CustomPropertyEditorClass(RolePropertyEditor.class)
	private ObjectProperty<Role> addUserToListOnContactDefaultGroup;
	private BooleanProperty autoSave;
	private BooleanProperty externalCommandCall;
	private BooleanProperty collectLastArguments;

	public Configuration() {
		this.botToken = new SimpleStringProperty();
		this.addUserToListOnContact = new SimpleBooleanProperty();
		this.addUserToListOnContactActivated = new SimpleBooleanProperty(true);
		this.addUserToListOnContactDefaultGroup = new SimpleObjectProperty<>();
		this.autoSave = new SimpleBooleanProperty(true);
		this.externalCommandCall = new SimpleBooleanProperty();
		this.collectLastArguments = new SimpleBooleanProperty(true);
	}

	public final StringProperty botTokenProperty() {
		return this.botToken;
	}

	public final String getBotToken() {
		return this.botTokenProperty().get();
	}

	public final void setBotToken(final String botToken) {
		this.botTokenProperty().set(botToken);
	}

	public final BooleanProperty addUserToListOnContactProperty() {
		return this.addUserToListOnContact;
	}

	public final boolean isAddUserToListOnContact() {
		return this.addUserToListOnContactProperty().get();
	}

	public final void setAddUserToListOnContact(final boolean addUserToListOnContact) {
		this.addUserToListOnContactProperty().set(addUserToListOnContact);
	}

	public BooleanProperty autoSaveProperty() {
		return this.autoSave;
	}

	public boolean isAutoSave() {
		return this.autoSaveProperty().get();
	}

	public void setAutoSave(final boolean autoSave) {
		this.autoSaveProperty().set(autoSave);
	}

	public BooleanProperty addUserToListOnContactActivatedProperty() {
		return this.addUserToListOnContactActivated;
	}

	public boolean isAddUserToListOnContactActivated() {
		return this.addUserToListOnContactActivatedProperty().get();
	}

	public void setAddUserToListOnContactActivated(final boolean addUserToListOnContactActivated) {
		this.addUserToListOnContactActivatedProperty().set(addUserToListOnContactActivated);
	}

	public ObjectProperty<Role> addUserToListOnContactDefaultGroupProperty() {
		return this.addUserToListOnContactDefaultGroup;
	}

	public Role getAddUserToListOnContactDefaultGroup() {
		return this.addUserToListOnContactDefaultGroupProperty().get();
	}

	public void setAddUserToListOnContactDefaultGroup(final Role addUserToListOnContactDefaultGroup) {
		this.addUserToListOnContactDefaultGroupProperty().set(addUserToListOnContactDefaultGroup);
	}

	public BooleanProperty externalCommandCallProperty() {
		return this.externalCommandCall;
	}

	public boolean isExternalCommandCall() {
		return this.externalCommandCallProperty().get();
	}

	public void setExternalCommandCall(final boolean externalCommandCall) {
		this.externalCommandCallProperty().set(externalCommandCall);
	}

	public BooleanProperty collectLastArgumentsProperty() {
		return this.collectLastArguments;
	}
	

	public boolean isCollectLastArguments() {
		return this.collectLastArgumentsProperty().get();
	}
	

	public void setCollectLastArguments(final boolean collectLastArguments) {
		this.collectLastArgumentsProperty().set(collectLastArguments);
	}
	
}
