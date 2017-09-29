/**
 * 
 */
package io.enoy.tbc.application.control.enoyfx.controlsfx;

import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.BeanPropertyUtils;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Enis.Oezsoy
 */
public class LocalizedPropertyItem extends CustomPropertyItem
{

	private String fieldIdentifier;
	private ResourceBundle bundle;

	public LocalizedPropertyItem(Item item, Class<?> clazz, ResourceBundle bundle)
	{
		super(item, clazz);
		this.bundle = bundle;
		this.fieldIdentifier = clazz.getName() + "." + item.getName();
	}

	@Override
	public String getCategory()
	{
		return bundle.getString(fieldIdentifier + ".category");
	}

	@Override
	public String getName()
	{
		return bundle.getString(fieldIdentifier + ".name");
	}

	@Override
	public String getDescription()
	{
		return bundle.getString(fieldIdentifier + ".description");
	}

	public String getFieldIdentifier()
	{
		return fieldIdentifier;
	}

	public static List<LocalizedPropertyItem> getLocalizedBeanProperties(ResourceBundle bundle, Object bean,
		Predicate<PropertyDescriptor> test)
	{
		List<LocalizedPropertyItem> items = BeanPropertyUtils.getProperties(bean, test)//
			.stream()//
			.map(item -> new LocalizedPropertyItem(item, bean.getClass(), bundle))//
			.collect(Collectors.toList());

		return items;
	}

	public static List<LocalizedPropertyItem> getLocalizedBeanProperties(ResourceBundle bundle, Object bean)
	{
		return getLocalizedBeanProperties(bundle, bean, p -> true);
	}

}
