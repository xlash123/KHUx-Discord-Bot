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
import xlash.bot.khux.sheduler.Event;
import xlash.bot.khux.sheduler.Scheduler;
import xlash.bot.khux.sheduler.TimedEvent;

public class KHUxBot {

	public static final String VERSION = "1.2.5";

	public static DiscordAPI api;

	public static MedalHandler medalHandler;
	public static TwitterHandler twitterHandler;
	public static CommandHandler commandHandler;
	public static Config config;
	public static Scheduler scheduler;

	public volatile static boolean shouldTwitterUpdate;
	public volatile static boolean shouldLuxNA;
	public volatile static boolean shouldLuxJP;

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
			}
			config.saveConfig();
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
		if(shouldTwitterUpdate){
			scheduler.enableTimedEvent("Twitter Update");
		}
		shouldLuxNA = api.getChannelById(config.luxChannelNA) != null;
		shouldLuxJP = api.getChannelById(config.luxChannelJP) != null;
		if(shouldLuxNA){
			scheduler.enableEvent("NA Lux On");
			scheduler.enableEvent("NA Lux Off");
		}
		if(shouldLuxJP){
			scheduler.enableEvent("JP Lux On");
			scheduler.enableEvent("JP Lux Off");
		}
	}

	public void initialize() {
		System.out.println("Initializing...");
		medalHandler = new MedalHandler();
		twitterHandler = new TwitterHandler();
		scheduler = new Scheduler();
		scheduler.addDisabledEvent(new Event(){

			@Override
			public String[] getTimes() {
				return new String[]{"03:00:00", "09:00:00"};
			}

			@Override
			public void run() {
				api.getChannelById(config.luxChannelNA).sendMessage("NA: " + config.luxOnPrompt);
			}

			@Override
			public String getName() {
				return "NA Lux On";
			}
			
		});
		scheduler.addDisabledEvent(new Event(){

			@Override
			public String[] getTimes() {
				return new String[]{"04:00:00", "10:00:00"};
			}

			@Override
			public void run() {
				api.getChannelById(config.luxChannelNA).sendMessage("NA: " + config.luxOffPrompt);
			}

			@Override
			public String getName() {
				return "NA Lux Off";
			}
			
		});
		scheduler.addDisabledEvent(new Event(){

			@Override
			public String[] getTimes() {
				return new String[]{"03:00:00", "13:00:00"};
			}

			@Override
			public void run() {
				api.getChannelById(config.luxChannelJP).sendMessage("JP: " + config.luxOnPrompt);
			}

			@Override
			public String getName() {
				return "JP Lux On";
			}
			
		});
		scheduler.addDisabledEvent(new Event(){

			@Override
			public String[] getTimes() {
				return new String[]{"04:00:00", "14:00:00"};
			}

			@Override
			public void run() {
				api.getChannelById(config.luxChannelJP).sendMessage("JP: " + config.luxOffPrompt);
			}

			@Override
			public String getName() {
				return "JP Lux Off";
			}
			
		});
		scheduler.addDisabledTimedEvent(new TimedEvent() {
			
			@Override
			public void run() {
				twitterHandler.getTwitterUpdate(api);
			}
			
			@Override
			public String getName() {
				return "Twitter Update";
			}
			
			@Override
			public int getFrequency() {
				return 3;
			}
		});
		scheduler.addTimedEvent(new TimedEvent() {
			
			@Override
			public void run() {
				config.saveConfig();
				findUpdate();
			}
			
			@Override
			public String getName() {
				return "Bot Update";
			}
			
			@Override
			public int getFrequency() {
				return 20;
			}
		});
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

}
