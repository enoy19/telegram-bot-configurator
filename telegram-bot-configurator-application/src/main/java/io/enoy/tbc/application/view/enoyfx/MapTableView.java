/**
 * 
 */
package io.enoy.tbc.application.view.enoyfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.concurrent.Callable;

/**
 * @author Enis.Oezsoy
 */
public class MapTableView<K, V> extends VBox
{

	private MapTable<K, V> mapTable;
	private StackPane addKeyPane;
	private StackPane addValuePane;
	private Button addButton;
	private Callable<K> keyCallable;
	private Callable<V> valueCallable;

	public MapTableView()
	{
		init(FXCollections.observableHashMap());
	}

	public MapTableView(ObservableMap<K, V> map, Callable<K> keyCallable, Callable<V> valueCallable)
	{
		init(map);

		this.keyCallable = keyCallable;
		this.valueCallable = valueCallable;
	}

	public MapTableView(ObservableMap<K, V> map, StringConverter<K> keyConverter, StringConverter<V> valueConverter)
	{
		init(map);

		setConverters(keyConverter, valueConverter);

	}

	public void setConverters(StringConverter<K> keyConverter, StringConverter<V> valueConverter)
	{
		TextField textFieldKey = new TextField();
		TextField textFieldValue = new TextField();

		textFieldKey.setOnAction(e -> textFieldValue.requestFocus());
		textFieldValue.setOnAction(e -> {
			addButton.fire();
			textFieldKey.requestFocus();
			textFieldKey.selectAll();
		});

		this.keyCallable = () -> keyConverter.fromString(textFieldKey.getText());
		this.valueCallable = () -> valueConverter.fromString(textFieldValue.getText());

		mapTable.getKeyColumn().setCellFactory(TextFieldTableCell.forTableColumn(keyConverter));
		mapTable.getValueColumn().setCellFactory(TextFieldTableCell.forTableColumn(valueConverter));

		addKeyPane.getChildren().setAll(textFieldKey);
		addValuePane.getChildren().setAll(textFieldValue);
	}

	private void init(ObservableMap<K, V> map)
	{
		setSpacing(5);

		mapTable = new MapTable<>(map);

		addKeyPane = new StackPane();
		addValuePane = new StackPane();

		addButton = new Button("+");
		addButton.setMinWidth(25);
		addButton.setOnAction(e -> {
			try
			{
				add();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
				// TODO: Dialog
			}
		});

		HBox.setHgrow(addKeyPane, Priority.ALWAYS);
		HBox.setHgrow(addValuePane, Priority.ALWAYS);

		VBox.setVgrow(mapTable, Priority.ALWAYS);

		HBox hBox = new HBox(5, addKeyPane, new Label(":"), addValuePane, addButton);
		hBox.setAlignment(Pos.CENTER);

		getChildren().setAll(mapTable, hBox);

	}

	public void add() throws Exception
	{
		// TODO: Update on override
		put(keyCallable.call(), valueCallable.call());
	}

	public void put(K key, V value)
	{
		mapTable.put(key, value);
	}

	public MapTable<K, V> getMapTable()
	{
		return mapTable;
	}

}
