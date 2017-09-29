/**
 * 
 */
package io.enoy.tbc.application.view.enoyfx;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import io.enoy.tbc.application.control.enoyfx.ControlUtils;

import java.util.Collection;
import java.util.Map.Entry;

/**
 * @author Enis.Oezsoy
 */
public class MapTable<K, V> extends TableView<Entry<K, V>>
	implements Callback<Collection<Entry<K, V>>, Void>
{

	private ObjectProperty<ObservableMap<K, V>> map;

	private InvalidationListener listener;

	private TableColumn<Entry<K, V>, K> keyColumn;

	private TableColumn<Entry<K, V>, V> valueColumn;

	private MenuItem deleteMenuItem;

	public MapTable()
	{
		this(FXCollections.observableHashMap());
	}

	public MapTable(ObservableMap<K, V> map)
	{
		this.map = new SimpleObjectProperty<>();

		keyColumn = new TableColumn<>("key");
		valueColumn = new TableColumn<>("value");

		this.setEditable(true);
		keyColumn.setEditable(true);
		valueColumn.setEditable(true);

		keyColumn//
			.setCellValueFactory(t -> new SimpleObjectProperty<K>(t.getValue().getKey()));
		valueColumn//
			.setCellValueFactory(t -> new SimpleObjectProperty<V>(t.getValue().getValue()));

		getColumns().add(keyColumn);
		getColumns().add(valueColumn);

		keyColumn//
			.setOnEditCommit(t -> {
				ObservableMap<K, V> tempMap = getMap();
				final K oldKey = t.getOldValue();
				final V oldValue = tempMap.get(oldKey);

				if (!tempMap.containsKey(t.getNewValue()))
				{
					tempMap.remove(oldKey);
					tempMap.put(t.getNewValue(), oldValue);
				}
				else
				{
					t.consume();
					updateTable();
				}

			});

		valueColumn//
			.setOnEditCommit(t -> {
				getMap().put(t.getRowValue().getKey(), t.getNewValue());
			});

		listener = o -> {
			if (getMap() != null)
			{
				this.getItems().setAll(FXCollections.observableArrayList(getMap().entrySet()));
			}
			else
			{
				this.getItems().clear();
			}
		};

		mapProperty().addListener((v, o, n) -> {
			if (o != null)
			{
				o.removeListener(listener);
			}

			if (n != null)
			{
				n.addListener(listener);
			}

			updateTable();
		});

		setMap(map);

		deleteMenuItem = ControlUtils.addDeleteMenuItemToContextMenu(this, this);
	}

	public void setMap(ObservableMap<K, V> map)
	{
		mapProperty().set(map);
	}

	public ObjectProperty<ObservableMap<K, V>> mapProperty()
	{
		return map;
	}

	public ObservableMap<K, V> getMap()
	{
		return map.get();
	}

	public void updateTable()
	{
		listener.invalidated(null);
	}

	public void put(K key, V value)
	{
		boolean alreadyContains = getMap().containsKey(key);
		getMap().put(key, value);
		if (alreadyContains)
			updateTable();
	}

	public TableColumn<Entry<K, V>, K> getKeyColumn()
	{
		return keyColumn;
	}

	public TableColumn<Entry<K, V>, V> getValueColumn()
	{
		return valueColumn;
	}

	public MenuItem getDeleteMenuItem()
	{
		return deleteMenuItem;
	}

	@Override
	public Void call(Collection<Entry<K, V>> param)
	{
		ObservableMap<K, V> tempMap = getMap();
		if (tempMap != null)
		{
			param.forEach(entry -> {
				tempMap.remove(entry.getKey());
			});
		}
		return null;
	}

}
