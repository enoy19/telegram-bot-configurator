/**
 *
 */
package io.enoy.tbc.application.control.enoyfx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Enis.Oezsoy
 */
public class ResourcePropertyBundle extends HashMap<String, StringProperty> {

	private static final long serialVersionUID = -6556088985143534904L;

	private String baseName;

	// TODO: boolean adapter ignore containskey (always true)

	// private boolean initializing;
	private ObjectProperty<Locale> locale;
	private ResourceBundle resourceBundle;

	public ResourcePropertyBundle(String baseName) {
		this.baseName = baseName;

		// initializing = true;
		locale = new SimpleObjectProperty<>(Locale.getDefault());
		locale.addListener((v, o, n) -> update());

		resourceBundle = new ResourcePropertyBundleAdapter();

		update();
		// initializing = false;
	}

	public void update() {
		ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale.get());

		bundle.keySet().forEach(s -> {
			if (!this.containsKey(s)) {
				this.put(s, new SimpleStringProperty(s));
			}
		});

		bundle.keySet().forEach(s -> {
			this.get(s).set(bundle.getString(s));
		});
	}

	// public void setBaseName(String baseName)
	// {
	// this.baseName = baseName;
	// }
	//
	// public String getBaseName()
	// {
	// return baseName;
	// }

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	@Override
	public StringProperty get(Object key) {
		if (!containsKey(key)) {
			this.put(key.toString(), new SimpleStringProperty(key.toString()));
			System.err.println("ResourcePropertyBundle: resource not found \"" + key + "\"");
		}
		return super.get(key);
	}

	// @Override
	// public boolean containsKey(Object key)
	// {
	// boolean contains = super.containsKey(key);
	// if (!initializing && !contains)
	// {
	// System.err.println("ResourcePropertyBundle (containsKey): resource not found \"" + key + "\"");
	// }
	// return contains;
	// }

	private class ResourcePropertyBundleAdapter extends ResourceBundle {

		@Override
		protected Object handleGetObject(String key) {
			return ResourcePropertyBundle.this.get(key).get();
		}

		@Override
		public boolean containsKey(String key) {
			return true;
		}

		@Override
		public Enumeration<String> getKeys() {
			IteratorEnumeration<String> keys = new IteratorEnumeration<>(ResourcePropertyBundle.this.keySet().iterator());
			return keys;
		}

	}

}
