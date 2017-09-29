/**
 * 
 */
package io.enoy.tbc.application.control.enoyfx.controlsfx;

import org.controlsfx.property.editor.PropertyEditor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Enis.Oezsoy
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomPropertyEditorClass
{
	public Class<? extends PropertyEditor<?>> value();
}
