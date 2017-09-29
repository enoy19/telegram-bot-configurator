package io.enoy.tbc.application.control.js.functions;

import java.util.function.Function;

public class Sleep implements Function<Long, Void>{

	@Override
	public Void apply(Long t) {
		t = t == null ? 0L : t;
		
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
