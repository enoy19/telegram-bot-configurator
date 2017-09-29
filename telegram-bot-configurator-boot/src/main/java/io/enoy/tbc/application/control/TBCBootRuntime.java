package io.enoy.tbc.application.control;

import io.enoy.tbc.boot.TelegramBotConfiguratorApplication;
import io.enoy.tbc.commons.TBCRuntime;
import org.springframework.stereotype.Component;

@Component
public class TBCBootRuntime implements TBCRuntime {

	@Override
	public boolean isFx() {
		return TelegramBotConfiguratorApplication.isFx();
	}

}
