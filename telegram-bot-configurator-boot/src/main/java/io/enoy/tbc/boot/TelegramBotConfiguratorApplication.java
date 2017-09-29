package io.enoy.tbc.boot;

import io.enoy.tbc.commons.TBCRuntime;
import io.enoy.tbc.application.control.SaveManager;
import io.enoy.tbc.application.control.concurrent.BotMessageHandler;
import io.enoy.tbc.application.view.TelegramBotConfiguratorApplicationFx;
import javafx.application.Application;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@Component
public class TelegramBotConfiguratorApplication {

	/* public static void main(String[] args) {
		SpringApplication.run(TelegramBotConfiguratorApplication.class, args);
	}*/

	private static final File PLUGINS_DIR = new File("tbc/plugins");

	private static boolean fx = false;

	@Autowired
	private SaveManager saveManager;
	@Autowired
	private BotMessageHandler botMessageHandler;

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("n", "nogui", false, "start without gui (file required)");
		options.addOption(null, "file", true, "open save/configuration file on start");
		options.addOption("?", "help", false, "print this message");

		CommandLine cmd = null;
		CommandLineParser parser = new DefaultParser();
		try {
			cmd = parser.parse(options, args);

			if (cmd.hasOption('?')) {
				HelpFormatter helpFormatter = new HelpFormatter();
				helpFormatter.printHelp("ant", options);
			} else {
				run(args, cmd);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private static void run(String[] args, CommandLine cmd) {
		loadPlugins();

		File file = null;
		if (cmd.hasOption("file")) {
			file = new File(cmd.getOptionValue("file"));
			if (!file.exists())
				file = null;
		}

		if (!cmd.hasOption('n')) {
			fx = true;
			Application.launch(TelegramBotConfiguratorApplicationFx.class, args);
		} else {
			ConfigurableApplicationContext context =
					SpringApplication.run(TelegramBotConfiguratorNoGuiConfiguration.class, args);

			try {
				context.getBean(TelegramBotConfiguratorApplication.class).startBot(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void loadPlugins() {
		if (!PLUGINS_DIR.exists()) {
			PLUGINS_DIR.mkdirs();
		}

		File[] jars = PLUGINS_DIR.listFiles((dir, name) -> {
			return dir.equals(PLUGINS_DIR) && name.endsWith(".jar");
		});

		URL[] jarUrls = new URL[jars.length];
		for (int i = 0; i < jars.length; i++) {
			try {
				jarUrls[i] = jars[i].toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				System.err.println("Could not load jar file: " + jars[i].getAbsolutePath());
				System.exit(1337);
			}
		}

		if (jarUrls.length > 0) {

			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URLClassLoader newClassLoader = new URLClassLoader(jarUrls, classLoader);
			Thread.currentThread().setContextClassLoader(newClassLoader);

		}

	}

	private void startBot(File file) throws Exception {
		if (saveManager.open(file)) {
			botMessageHandler.call();
		} else {
			System.err.println("Failed to open file");
			System.exit(1);
		}
	}

	public static boolean isFx() {
		return fx;
	}

}
