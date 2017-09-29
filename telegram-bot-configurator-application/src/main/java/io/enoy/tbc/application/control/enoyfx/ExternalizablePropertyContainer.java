package io.enoy.tbc.application.control.enoyfx;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public interface ExternalizablePropertyContainer extends Externalizable {

	@SuppressWarnings("unchecked")
	@Override
	default void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int size = in.readInt();

		String fieldName;
		Object value;

		Map<String, Field> allFields = null;

		for (int i = 0; i < size; i++) {
			fieldName = (String) in.readObject();
			String[] fieldNames = fieldName.split(";");
			value = in.readObject();
			try {
				Field propertyField = null;
				try {
					propertyField = getClass().getDeclaredField(fieldNames[0]);
				} catch (NoSuchFieldException e) {
					if (allFields == null) {
						allFields = getAllFieldsAsMap();
					}
					propertyField = allFields.get(fieldNames[1]);
				}

				if (propertyField != null) {

					Class<?> propertyType = propertyField.getType();
					propertyField.setAccessible(true);
					Object propertyObject = propertyField.get(this);

					if (Collection.class.isAssignableFrom(propertyType)) {
						Collection<Object> set = (Collection<Object>) propertyObject;
						Collection<Object> valueSet = (Collection<Object>) value;
						valueSet.forEach(set::add);
					} else if (Map.class.isAssignableFrom(propertyType)) {
						Map<Object, Object> map = (Map<Object, Object>) propertyObject;
						Map<Object, Object> valueMap = (Map<Object, Object>) value;
						valueMap.forEach(map::put);
					} else {
						WritableValue<Object> writable = (WritableValue<Object>) propertyObject;
						writable.setValue(value);
					}

				}
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	default void writeExternal(ObjectOutput out) throws IOException {
		List<Field> fields = getAllFields();
		List<Field> obsFields = fields.stream()//
				.filter(f -> ObservableValue.class.isAssignableFrom(f.getType())//
						|| ObservableSet.class.isAssignableFrom(f.getType())//
						|| ObservableList.class.isAssignableFrom(f.getType())//
						|| ObservableMap.class.isAssignableFrom(f.getType()))//
				.collect(Collectors.toList());

		out.writeInt(obsFields.size());

		for (Field field : obsFields) {
			try {
				field.setAccessible(true);
				Object fieldObject = field.get(this);

				out.writeObject(field.getName() + ";" + getFullFieldName(field));
				Object writeObject = null;

				if (fieldObject instanceof Set) {
					Set<Object> fieldSet = (Set<Object>) fieldObject;
					Set<Object> set = new HashSet<>(fieldSet);
					writeObject = set;
				} else if (fieldObject instanceof List) {
					List<Object> fieldList = (List<Object>) fieldObject;
					List<Object> list = new ArrayList<>(fieldList);
					writeObject = list;
				} else if (fieldObject instanceof Map) {
					Map<Object, Object> fieldMap = (Map<Object, Object>) fieldObject;
					Map<Object, Object> map = new HashMap<>(fieldMap);
					writeObject = map;
				} else {
					ObservableValue<Object> observable = (ObservableValue<Object>) fieldObject;
					writeObject = observable.getValue();
				}

				out.writeObject(writeObject);

			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	default List<Field> getAllFields() {
		List<Field> fields = new ArrayList<>();
		return getAllFields(fields, getClass());
	}

	default List<Field> getAllFields(List<Field> fields, Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null) {
			getAllFields(fields, type.getSuperclass());
		}

		return fields;
	}

	default Map<String, Field> getAllFieldsAsMap() {
		Map<String, Field> map = new HashMap<>();
		return getAllFieldsAsMap(map, getClass());
	}

	default Map<String, Field> getAllFieldsAsMap(Map<String, Field> fields, Class<?> type) {
		Arrays.asList(type.getDeclaredFields()).forEach(f -> {
			fields.put(getFullFieldName(f), f);
		});

		if (type.getSuperclass() != null) {
			getAllFieldsAsMap(fields, type.getSuperclass());
		}

		return fields;
	}

	default String getFullFieldName(Field f) {
		return f.getDeclaringClass().getName() + "." + f.getName();
	}

}
