package io.enoy.tbc.application.control.js.functions;

import java.util.function.Function;

public class Log implements Function<Object, Void>{

	@Override
	public Void apply(Object arg0) {
		System.out.println(arg0 == null ? "null" : arg0.toString());
		return null;
	}

}
