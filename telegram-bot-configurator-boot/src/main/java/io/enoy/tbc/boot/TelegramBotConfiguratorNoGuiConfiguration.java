package io.enoy.tbc.boot;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@EnableAutoConfiguration
@ComponentScan(value = {"io.enoy.tbc.application.control", "io.enoy.tbc.application.model", "io.enoy.tbc.boot"},
		excludeFilters = @Filter(type = FilterType.REGEX, pattern = "io\\.enoy\\.tbc\\.application\\.view.*"))
public class TelegramBotConfiguratorNoGuiConfiguration {

}
