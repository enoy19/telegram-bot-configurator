package io.enoy.tbc.application.control.enoyfx;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PropertyModel<T> {

	private Class<T> clazz;
	private T bean;
	
	private List<Runnable> saves;
	private List<Runnable> reverts;
	
	@SuppressWarnings("unchecked")
	public PropertyModel(T bean) {
		this.bean = bean;
		this.clazz = (Class<T>) bean.getClass();
		
		Field[] fields = clazz.getDeclaredFields();
		
		this.saves = new ArrayList<>(fields.length);
		this.reverts = new ArrayList<>(fields.length);
		
		for (Field field : fields) {
			try {
				Field propertyField = getClass().getField(field.getName());
				Object property = propertyField.get(this);
				Runnable revert = ()->{
					WritableValue<Object> writableValue = (WritableValue<Object>) property;
					try {
						writableValue.setValue(field.get(bean));
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
				Runnable save = ()->{
					ObservableValue<Object> observable = (ObservableValue<Object>) property;
					try {
						field.set(bean, observable.getValue());
					} catch (Exception e) {
						e.printStackTrace();
					};
				};
				
				reverts.add(revert);
				saves.add(save);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void revert(){
		reverts.forEach(r->r.run());
	}
	
	public void save(){
		saves.forEach(r->r.run());
	}
	
	public T getBean() {
		return bean;
	}
	
	public Class<T> getClazz() {
		return clazz;
	}
	
}
