/**
 * 
 */
package io.enoy.tbc.application.control.enoyfx;

import javafx.beans.property.ObjectProperty;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Enis.Oezsoy
 */
public class ControlUtils
{

	@SuppressWarnings("unchecked")
	public static <T> void addDeleteMenuItemFunctionality(MultipleSelectionModel<T> selectionModel,
		ObjectProperty<? extends Collection<T>> collectionProperty, Predicate<List<T>> deletePredicate,
		Callback<Collection<T>, Void> onRemoved, MenuItem menuItem)
	{
		menuItem.setOnAction(e -> {
			if (collectionProperty.get() != null)
			{
				Collection<T> collection = collectionProperty.get();
				List<T> selectedItems = selectionModel.getSelectedItems();
				List<T> toBeRemoved = new ArrayList<>(selectedItems);
				if (!selectedItems.isEmpty())
				{
					if (deletePredicate.test(toBeRemoved))
					{
						if (collection instanceof SortedList)
						{
							collection = (Collection<T>) ( (SortedList<T>) collection ).getSource();
						}
						collection.removeAll(toBeRemoved);
						if (onRemoved != null)
						{
							onRemoved.call(toBeRemoved);
						}
					}
				}
			}
		});
	}

	public static <T> MenuItem addDeleteMenuItemToContextMenu(TableView<T> table, Predicate<List<T>> deletePredicate,
		Callback<Collection<T>, Void> onRemoved)
	{
		MenuItem deleteMenuItem = addDeleteMenuItemToContextMenu((Control) table);
		addDeleteMenuItemFunctionality(table.getSelectionModel(), table.itemsProperty(), deletePredicate, onRemoved,
			deleteMenuItem);

		return deleteMenuItem;
	}

	public static <T> MenuItem addDeleteMenuItemToContextMenu(TableView<T> table)
	{
		return addDeleteMenuItemToContextMenu(table, null);
	}

	public static <T> MenuItem addDeleteMenuItemToContextMenu(TableView<T> table, Callback<Collection<T>, Void> onRemoved)
	{
		return addDeleteMenuItemToContextMenu(table, l -> true, onRemoved);
	}

	/**
	 * appends delete menuitem to context menu
	 * @param listView
	 * @param deletePredicate
	 */
	public static <T> MenuItem addDeleteMenuItemToContextMenu(ListView<T> listView, Predicate<List<T>> deletePredicate,
		Callback<Collection<T>, Void> onRemoved)
	{
		MenuItem deleteMenuItem = addDeleteMenuItemToContextMenu((Control) listView);
		addDeleteMenuItemFunctionality(listView.getSelectionModel(), listView.itemsProperty(), deletePredicate, onRemoved,
			deleteMenuItem);

		return deleteMenuItem;
	}

	private static MenuItem addDeleteMenuItemToContextMenu(Control control)
	{
		ContextMenu contextMenu = control.getContextMenu();

		if (contextMenu == null)
		{
			contextMenu = new ContextMenu();
			control.setContextMenu(contextMenu);
		}

		MenuItem deleteMenuItem = new MenuItem("delete");
		contextMenu.getItems().add(deleteMenuItem);

		return deleteMenuItem;
	}

	public static <T> MenuItem addDeleteMenuItemToContextMenu(ListView<T> listView)
	{
		return addDeleteMenuItemToContextMenu(listView, null);
	}

	public static <T> MenuItem addDeleteMenuItemToContextMenu(ListView<T> listView, Callback<Collection<T>, Void> onRemoved)
	{
		return addDeleteMenuItemToContextMenu(listView, l -> true, onRemoved);
	}

}
