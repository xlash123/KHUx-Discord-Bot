package xlash.bot.khux;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import de.btobastian.javacord.listener.server.ServerJoinListener;
import de.btobastian.javacord.listener.server.ServerLeaveListener;
import xlash.bot.khux.TwitterHandler.Tweet;
import xlash.bot.khux.commands.AdminCommand;
import xlash.bot.khux.commands.CommandHandler;
import xlash.bot.khux.commands.ConfigCommand;
import xlash.bot.khux.commands.UnAdmin;
import xlash.bot.khux.commands.UnionCrossCommand;
import xlash.bot.khux.commands.DefaultCommand;
import xlash.bot.khux.commands.HelpCommand;
import xlash.bot.khux.commands.LuxCommand;
import xlash.bot.khux.commands.MedalCommand;
import xlash.bot.khux.commands.MedalJPCommand;
import xlash.bot.khux.commands.MedalListCommand;
import xlash.bot.khux.commands.MedalNACommand;
import xlash.bot.khux.commands.RefreshCommand;
import xlash.bot.khux.commands.ResetCommand;
import xlash.bot.khux.commands.SaltCommand;
import xlash.bot.khux.commands.TweetCommand;
import xlash.bot.khux.config.BotConfig;
import xlash.bot.khux.config.ServerConfig;
import xlash.bot.khux.sheduler.Event;
import xlash.bot.khux.sheduler.Scheduler;
import xlash.bot.khux.sheduler.TimedEvent;
import xlash.bot.khux.util.BonusTimes;

public class KHUxBot {

	public static final String VERSION = "1.5.1";

	public static DiscordAPI api;

	public static MedalHandler medalHandler;
	public static TwitterHandler twitterHandler;
	public static CommandHandler commandHandler;
	public static BotConfig botConfig;
	public static ArrayList<ServerConfig> serverConfigs = new ArrayList<ServerConfig>();
	public static Scheduler scheduler;

	public static final String[] COMEBACKS = new String[]{"Don't at me, bro.", "42", "no", "https://youtu.be/dQw4w9WgXcQ", "Why would I know?", "*I am a bot, and this action was performed automatically.*", "Yes", "Ask again later", "I'm not your mom.", "Do me a favor and stop asking for favors", "KH3 will release in 2020", "Whoooaaa! Looking cool, Joker!", "I dare you to hack me. My IP is 127.0.0.1"};

	/**
	 * Starts the bot. If you're running this in a development environment, make sure you are 
	 * running it with parameter "run", else nothing will happen.
	 * @param args
	 */
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
				System.out.println("If you're reading this, run with argument 'run'.");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Running bot version: " + VERSION);
			findUpdate();
			botConfig = new BotConfig();
			botConfig.loadConfig();
			if (botConfig.botToken == null || botConfig.botToken.isEmpty()) {
				System.out.println("This is your first time running this bot. Thanks for installing!");
				System.out.println("To being using the bot, please enter your bot token.");
				System.out.println("If you need to make changes later, go to the config file in 'khuxbot config/config.properties'.");
				System.out.print("Enter token: ");
				Scanner in = new Scanner(System.in);
				botConfig.botToken = in.nextLine();
				in.close();
			}
			botConfig.saveConfig();
			new KHUxBot();
		}
	}

	public KHUxBot() {
		this.initialize();
		api = Javacord.getApi(botConfig.botToken, true);
		api.setAutoReconnect(false);
		commandHandler = new CommandHandler();
		registerCommands();
		
		connect(api);
		System.out.println("Bot setup complete! Connecting to servers...");
	}
	
	public void initializeServer(Server newServer){
		ServerConfig config = getServerConfig(newServer);
		if(config==null){
			config = new ServerConfig(newServer);
			config.saveConfig();
			serverConfigs.add(config);
		}
	}

	public void initialize() {
		System.out.println("Initializing...");
		medalHandler = new MedalHandler();
		twitterHandler = new TwitterHandler();
		scheduler = new Scheduler();
		scheduler.addEvent(new Event("NA Lux On", true, GameEnum.NA, BonusTimes.doubleLuxStartNA){
			@Override
			public void run() {
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.luxChannelNA.isEmpty()){
						Channel channel = server.getChannelById(config.luxChannelNA);
						if(channel != null){
							channel.sendMessage("NA: " + config.luxOnPrompt);
						}
					}
				}
			}
		});
		scheduler.addEvent(new Event("NA Lux Off", true, GameEnum.NA, BonusTimes.doubleLuxStopNA){
			@Override
			public void run() {
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.luxChannelNA.isEmpty()){
						Channel channel = server.getChannelById(config.luxChannelNA);
						if(channel != null){
							channel.sendMessage("NA: " + config.luxOffPrompt);
						}
					}
				}
			}
		});
		scheduler.addEvent(new Event("JP Lux On", true, GameEnum.JP, BonusTimes.doubleLuxStartJP){
			@Override
			public void run() {
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.luxChannelJP.isEmpty()){
						Channel channel = server.getChannelById(config.luxChannelJP);
						if(channel != null){
							channel.sendMessage("JP: " + config.luxOnPrompt);
						}
					}
				}
			}
		});
		scheduler.addEvent(new Event("JP Lux Off", true, GameEnum.JP, BonusTimes.doubleLuxStopJP){
			@Override
			public void run() {
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.luxChannelJP.isEmpty()){
						Channel channel = server.getChannelById(config.luxChannelJP);
						if(channel != null){
							channel.sendMessage("JP: " + config.luxOffPrompt);
						}
					}
				}
			}
		});
		scheduler.addTimedEvent(new TimedEvent("Twitter Update NA", true, 2) {
			@Override
			public void run() {
				ArrayList<Tweet> tweets = twitterHandler.getNewTwitterLinks(GameEnum.NA);
				if(tweets.isEmpty()) return;
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.updateChannelNA.isEmpty()){
						Channel channel = server.getChannelById(config.updateChannelNA);
						if(channel != null){
							twitterHandler.sendTwitterUpdate(channel, tweets, GameEnum.NA);
						}
					}
				}
			}
		});
		scheduler.addTimedEvent(new TimedEvent("Twitter Update JP", true, 2) {
			@Override
			public void run() {
				ArrayList<Tweet> tweets = twitterHandler.getNewTwitterLinks(GameEnum.JP);
				if(tweets.isEmpty()) return;
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.updateChannelNA.isEmpty()){
						Channel channel = server.getChannelById(config.updateChannelJP);
						if(channel != null){
							twitterHandler.sendTwitterUpdate(channel, tweets, GameEnum.JP);
						}
					}
				}
			}
		});
		scheduler.addTimedEvent(new TimedEvent("Bot Update", true, 20) {
			@Override
			public void run() {
				findUpdate();
			}
		});
		scheduler.addEvent(new Event("Refresh", true, GameEnum.NA, "05:00:00"){
			@Override
			public void run() {
				medalHandler.refreshMedalList();
			}
		});
		scheduler.addTimedEvent(new TimedEvent("Reminders", true, 1) {
			@Override
			public void run() {
				int luxTimeDifNA = BonusTimes.luxTimeDifference(GameEnum.NA);
				int luxTimeDifJP = BonusTimes.luxTimeDifference(GameEnum.JP);
				if(luxTimeDifNA < 30 || luxTimeDifJP < 30) {
					for(ServerConfig config : serverConfigs) {
						if(config.luxRemind>0) {
							if(!config.luxChannelNA.isEmpty()) {
								if(config.luxRemind == luxTimeDifNA) {
									api.getChannelById(config.luxChannelNA).sendMessage("NA Reminder: Double lux in " + luxTimeDifNA + " minutes!");
								}
							}
						
							if(!config.luxChannelJP.isEmpty()) {
								if(config.luxRemind == luxTimeDifJP) {
									api.getChannelById(config.luxChannelNA).sendMessage("JP Reminder: Double lux in " + luxTimeDifJP + " minutes!");
								}
							}
						}
					}
				}
				
				int uxTimeDifNA = BonusTimes.uxTimeDifference(GameEnum.NA);
				int uxTimeDifJP = BonusTimes.uxTimeDifference(GameEnum.JP);
				if(uxTimeDifNA < 30 || uxTimeDifJP < 30) {
					for(ServerConfig config : serverConfigs) {
						if(config.uxRemind>0) {
							if(!config.uxChannelNA.isEmpty()) {
								if(config.uxRemind == luxTimeDifNA) {
									api.getChannelById(config.uxChannelNA).sendMessage("NA Reminder: Union Cross in " + uxTimeDifNA + " minutes!");
								}
							}
						
							if(!config.luxChannelJP.isEmpty()) {
								if(config.luxRemind == luxTimeDifJP) {
									api.getChannelById(config.uxChannelNA).sendMessage("JP Reminder: Union Cross in " + uxTimeDifJP + " minutes!");
								}
							}
						}
					}
				}
			}
		});
		System.out.println("Initialization finished!");
	}
	
	/**
	 * Returns the ServerConfig class for the specified server.
	 * @param server to grab config for
	 * @return ServerConfig, or null if not registered.
	 */
	public static ServerConfig getServerConfig(Server server){
		String id = server.getId();
		for(ServerConfig config : serverConfigs){
			if(id.equals(config.serverId)){
				return config;
			}
		}
		return null;
	}
	
	public void registerCommands(){
		commandHandler.registerCommand(new HelpCommand());
		commandHandler.registerCommand(new MedalCommand());
		commandHandler.registerCommand(new MedalNACommand());
		commandHandler.registerCommand(new MedalJPCommand());
		commandHandler.registerCommand(new MedalListCommand());
		commandHandler.registerCommand(new LuxCommand());
		commandHandler.registerCommand(new TweetCommand());
		commandHandler.registerCommand(new RefreshCommand());
		commandHandler.registerCommand(new ResetCommand());
		commandHandler.registerCommand(new DefaultCommand());
		commandHandler.registerCommand(new ConfigCommand());
		commandHandler.registerCommand(new AdminCommand());
		commandHandler.registerCommand(new UnAdmin());
		commandHandler.registerCommand(new SaltCommand());
		commandHandler.registerCommand(new UnionCrossCommand());
	}

	public void connect(DiscordAPI api) {
		api.connect(new FutureCallback<DiscordAPI>() {
			public void onSuccess(DiscordAPI api) {
				for(Server server : api.getServers()){
					initializeServer(server);
				}
				api.registerListener(new MessageCreateListener(){

					@Override
					public void onMessageCreate(DiscordAPI api, Message message) {
						commandHandler.executeCommand(message);
						for(User u : message.getMentions()){
							if(u.isYourself()){
								Random rand = new Random();
								int i = rand.nextInt(COMEBACKS.length);
								message.reply(COMEBACKS[i]);
							}
						}
					}
					
				});
				
				api.registerListener(new ServerJoinListener() {
					
					@Override
					public void onServerJoin(DiscordAPI api, Server server) {
						System.out.println("Got new server " + server.getName() + ":" + server.getId());
						initializeServer(server);
					}
				});
				
				api.registerListener(new ServerLeaveListener() {
					@Override
					public void onServerLeave(DiscordAPI api, Server server) {
						synchronized(serverConfigs){
							System.out.println("Removing " + server.getName());
							serverConfigs.remove(getServerConfig(server));
						}
					}
				});
				
				scheduler.startThread();
				System.out.println("Connected to " + api.getServers().size() + " servers:");
				for(Server server : api.getServers()){
					System.out.println(">" + server.getName());
				}
				if(!VERSION.equals(botConfig.version)) {
					botConfig.version = VERSION;
					botConfig.saveConfig();
					for(Server server: api.getServers()) {
						String channelId = "";
						ServerConfig config = getServerConfig(server);
						if(!config.updateChannelNA.isEmpty()) {
							channelId = config.updateChannelNA;
						}else if(!config.updateChannelJP.isEmpty()) {
							channelId = config.updateChannelJP;
						}else {
							for(Channel channel : server.getChannels()) {
								if(channel.getName().equals("general")) {
									channelId = channel.getId();
								}
							}
						}
						if(!channelId.isEmpty()) {
							server.getChannelById(channelId).sendMessage("Bot Update: " + VERSION + "\nAdded !uc for Union Cross reminders.\nFixed issues with JP lux reminders.\nSwitched to using Twitter API for getting Tweets.");
						}
					}
				}
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
						"New update available. Download at - https://github.com/xlash123/KHUx-Discord-Bot/releases");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
