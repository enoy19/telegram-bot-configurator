package io.enoy.tbc.application.control.enoyfx;

import java.util.Enumeration;
import java.util.Iterator;

class IteratorEnumeration<E> implements Enumeration<E> {

	private final Iterator<E> iterator;

	public IteratorEnumeration(Iterator<E> iterator) {
		this.iterator = iterator;
	}

	public E nextElement() {
		return iterator.next();
	}

	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

}