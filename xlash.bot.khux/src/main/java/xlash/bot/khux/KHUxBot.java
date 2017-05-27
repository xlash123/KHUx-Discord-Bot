package xlash.bot.khux;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import xlash.bot.khux.commands.CommandHandler;
import xlash.bot.khux.commands.ConfigCommand;
import xlash.bot.khux.commands.DefaultCommand;
import xlash.bot.khux.commands.LuxCommand;
import xlash.bot.khux.commands.MedalCommand;
import xlash.bot.khux.commands.MedalJPCommand;
import xlash.bot.khux.commands.MedalNACommand;
import xlash.bot.khux.commands.RefreshCommand;
import xlash.bot.khux.commands.ResetCommand;
import xlash.bot.khux.commands.TweetCommand;
import xlash.bot.khux.config.Config;

public class KHUxBot {

	public static final String VERSION = "1.2.3";

	public static DiscordAPI api;

	public static MedalHandler medalHandler;
	public static TwitterHandler twitterHandler;
	public static CommandHandler commandHandler;
	public static Config config;

	public volatile static boolean shouldTwitterUpdate;
	public volatile static boolean shouldLux;

	public static void main(String[] args) {
		if (args.length == 0) {
			String runningFile;
			try {
				runningFile = new File(
						KHUxBot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
								.getAbsolutePath();
				ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start", "java", "-jar",
						"\"" + runningFile + "\"", "run");
				builder.redirectErrorStream(true);
				builder.start();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Running bot version: " + VERSION);
			findUpdate();
			config = new Config();
			config.loadConfig();
			if (config.botToken == null || config.botToken.isEmpty()) {
				System.out.println("This is your first time running this bot. Thanks for installing!");
				System.out.println("To being using the bot, please enter your bot token.");
				System.out.println(
						"If you need to make changes later, go to the config file in 'khuxbot config/config.properties'.");
				Scanner in = new Scanner(System.in);
				config.botToken = in.nextLine();
				in.close();
				config.saveConfig();
			}
			new KHUxBot();
		}
	}

	public KHUxBot() {
		this.initialize();
		api = Javacord.getApi(config.botToken, true);
		api.setAutoReconnect(false);
		commandHandler = new CommandHandler();
		registerCommands();
		connect(api);
		System.out.println("Waiting for server response...");
		while (api.getServers().size() == 0) {
		}
		System.out.println("Server connected! Let's go!");
		shouldTwitterUpdate = api.getChannelById(config.updateChannel) != null;
		shouldLux = api.getChannelById(config.luxChannel) != null;
		Thread grabTwitterUpdate = new Thread("Grab Twitter Update") {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (Integer.parseInt(getGMTTime("mm")) % 2 == 0 && getGMTTime("ss").equals("05")) {
						twitterHandler.getTwitterUpdate(api);
					}
				}
			}
		};
		grabTwitterUpdate.start();
		Thread botUpdate = new Thread("Bot Update") {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(600000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					config.saveConfig();
					findUpdate();
				}
			}
		};
		botUpdate.start();
		Thread luxTimes = new Thread("Lux Times") {
			@Override
			public void run() {
				while (true) {
					if (shouldLux) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (config.defaultGame == GameEnum.NA) {
							if (getGMTTime("hh:mm:ss").equals("09:00:00")
									|| getGMTTime("hh:mm:ss").equals("03:00:00")) {
								if (shouldLux)
									api.getChannelById(config.luxChannel).sendMessage(config.luxOnPrompt);
							} else if (getGMTTime("hh:mm:ss").equals("10:00:00")
									|| getGMTTime("hh:mm:ss").equals("04:00:00")) {
								if (shouldLux)
									api.getChannelById(config.luxChannel).sendMessage(config.luxOffPrompt);
							}
						} else{
							if (getGMTTime("hh:mm:ss").equals("03:00:00")
									|| getGMTTime("hh:mm:ss").equals("04:00:00")) {
								if (shouldLux)
									api.getChannelById(config.luxChannel).sendMessage(config.luxOnPrompt);
							} else if (getGMTTime("hh:mm:ss").equals("13:00:00")
									|| getGMTTime("hh:mm:ss").equals("14:00:00")) {
								if (shouldLux)
									api.getChannelById(config.luxChannel).sendMessage(config.luxOffPrompt);
							}
						}
					}
				}
			}
		};
		luxTimes.start();
	}

	public void initialize() {
		System.out.println("Initializing...");
		medalHandler = new MedalHandler();
		twitterHandler = new TwitterHandler();
		System.out.println("Initialization finished!");
	}
	
	public void registerCommands(){
		commandHandler.registerCommand(new MedalCommand());
		commandHandler.registerCommand(new MedalNACommand());
		commandHandler.registerCommand(new MedalJPCommand());
		commandHandler.registerCommand(new LuxCommand());
		commandHandler.registerCommand(new TweetCommand());
		commandHandler.registerCommand(new RefreshCommand());
		commandHandler.registerCommand(new ResetCommand());
		commandHandler.registerCommand(new DefaultCommand());
		commandHandler.registerCommand(new ConfigCommand());
	}

	public void connect(DiscordAPI api) {
		api.connect(new FutureCallback<DiscordAPI>() {
			public void onSuccess(DiscordAPI api) {
				api.registerListener(new MessageCreateListener(){

					@Override
					public void onMessageCreate(DiscordAPI api, Message message) {
						commandHandler.executeCommand(message);
					}
					
				});
			}

			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});
	}

	public static void findUpdate() {
		try {
			Document doc = Jsoup.connect("https://github.com/xlash123/KHUx-Discord-Bot/releases").get();
			String newVersion = doc.getElementsByClass("css-truncate-target").get(0).text();
			if (!VERSION.equals(newVersion)) {
				System.out.println(
						"New update avaialbe. Download at - https://github.com/xlash123/KHUx-Discord-Bot/releases");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getGMTTime(String format) {
		final Date currentTime = new Date();

		final SimpleDateFormat sdf = new SimpleDateFormat(format);

		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(currentTime);
	}

	public static String getGMTTime() {
		return getGMTTime("HH:mm:ss");
	}

}
