/**
 * 
 */
package io.enoy.tbc.application.view.enoyfx;

import javafx.collections.FXCollections;
import javafx.util.converter.DefaultStringConverter;

/**
 * @author Enis.Oezsoy
 */
public class StringMapTableView extends MapTableView<String, String>
{

	public StringMapTableView()
	{
		super(FXCollections.observableHashMap(), new DefaultStringConverter(), new DefaultStringConverter());
	}

}
