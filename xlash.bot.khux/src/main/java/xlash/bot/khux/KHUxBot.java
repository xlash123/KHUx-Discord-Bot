package xlash.bot.khux;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import xlash.bot.khux.commands.AdminCommand;
import xlash.bot.khux.commands.CommandHandler;
import xlash.bot.khux.commands.ConfigCommand;
import xlash.bot.khux.commands.UnAdmin;
import xlash.bot.khux.commands.UnionCrossCommand;
import xlash.bot.khux.commands.DefaultCommand;
import xlash.bot.khux.commands.HelpCommand;
import xlash.bot.khux.commands.KeybladeCommand;
import xlash.bot.khux.commands.LuxCommand;
import xlash.bot.khux.commands.MedalCommand;
import xlash.bot.khux.commands.MedalJPCommand;
import xlash.bot.khux.commands.MedalNACommand;
import xlash.bot.khux.commands.SaltCommand;
import xlash.bot.khux.commands.TweetCommand;
import xlash.bot.khux.config.BotConfig;
import xlash.bot.khux.config.ServerConfig;
import xlash.bot.khux.keyblades.KeybladeHandler;
import xlash.bot.khux.medals.MedalHandler;
import xlash.bot.khux.sheduler.Event;
import xlash.bot.khux.sheduler.Scheduler;
import xlash.bot.khux.sheduler.TimedEvent;
import xlash.bot.khux.util.BonusTimes;

/**
 * The instance of the KHUx Bot
 * @author xlash123
 *
 */
public class KHUxBot {

	public static final String VERSION = "1.11.1";
	public static final String CHANGELOG = "Minor bug fix.";

	/** Instance of the Discord API*/
	public static DiscordApi api;

	/** Instance of the medal handler*/
	public static MedalHandler medalHandler;
	/** Instance of the twitter handler*/
	public static TwitterHandler twitterHandler;
	/** Instance of the command handler*/
	public static CommandHandler commandHandler;
	/** Instance of the keyblade handler*/
	public static KeybladeHandler keybladeHandler;
	/** Instance of the bot config*/
	public static BotConfig botConfig;
	/** The list of config files of connected servers*/
	public static ArrayList<ServerConfig> serverConfigs = new ArrayList<ServerConfig>();
	/** Instance of the scheduler*/
	public static Scheduler scheduler;
	/** The list of all pending action messages*/
	public static ArrayList<ActionMessage> actionMessages = new ArrayList<>();

	/** Fun comebacks that the bot responds with when you @mention it*/
	public static final String[] COMEBACKS = new String[]{"Don't at me, bro.", "42", "no", "https://youtu.be/dQw4w9WgXcQ", "Why would I know?", "*I am a bot, and this action was performed automatically.*", "Yes", "Ask again later", "I'm not your mom.", "Do me a favor and stop asking for favors", "KH3 will release in 2020", "Whoooaaa! Looking cool, Joker!", "I dare you to hack me. My IP is 127.0.0.1"};

	/**
	 * Starts the bot. If you're running this in a development environment, make sure you are 
	 * running it with parameter "run", or else nothing will happen.
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) startCmd();
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
	
	public static boolean startCmd() {
		String runningFile;
		try {
			File file = new File(KHUxBot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			if(!file.isDirectory()) {
				runningFile = file.getPath();
				ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start", "java", "-jar",
						"\"" + runningFile + "\"", "run");
				builder.redirectErrorStream(true);
				builder.start();
				System.exit(0);
				return true;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * Creates a new object of only the greatest KHUx Discord Bot known to mankind. Please only create one instance.
	 */
	public KHUxBot() {
		this.initialize();
		api = new DiscordApiBuilder().setToken(botConfig.botToken).login().join();
		commandHandler = new CommandHandler();
		registerCommands();
		connect(api);
		System.out.println("Bot setup complete! Connecting to servers...");
	}
	
	/**
	 * Runs whenever a server connects.
	 * @param newServer the server that connected
	 */
	public void initializeServer(Server newServer){
		ServerConfig config = getServerConfig(newServer);
		if(config==null){
			config = new ServerConfig(newServer);
			config.saveConfig();
			serverConfigs.add(config);
		}
	}

	/**
	 * Initializes various components of the bot
	 */
	public void initialize() {
		System.out.println("Initializing...");
		medalHandler = new MedalHandler();
		twitterHandler = new TwitterHandler();
		keybladeHandler = new KeybladeHandler();
		scheduler = new Scheduler();
		//I apologize in advance for the following code dealing with events. 3 many anonymous classes 5 me
		scheduler.addEvent(new Event("NA Lux On", true, GameEnum.NA, BonusTimes.doubleLuxStartNA){
			@Override
			public void run(String currentTime) {
				setLux(GameEnum.NA, true, currentTime);
			}
		});
		scheduler.addEvent(new Event("NA Lux Off", true, GameEnum.NA, BonusTimes.doubleLuxStopNA){
			@Override
			public void run(String currentTime) {
				setLux(GameEnum.NA, false, currentTime);
			}
		});
		scheduler.addEvent(new Event("JP Lux On", true, GameEnum.JP, BonusTimes.doubleLuxStartJP){
			@Override
			public void run(String currentTime) {
				setLux(GameEnum.JP, true, currentTime);
			}
		});
		scheduler.addEvent(new Event("JP Lux Off", true, GameEnum.JP, BonusTimes.doubleLuxStopJP){
			@Override
			public void run(String currentTime) {
				setLux(GameEnum.JP, false, currentTime);
			}
		});
		scheduler.addEvent(new Event("NA UX On", true, GameEnum.NA, BonusTimes.uxBonusStartNA){
			@Override
			public void run(String currentTime) {
				setUX(GameEnum.NA, true, currentTime);
			}
		});
		scheduler.addEvent(new Event("NA UX Off", true, GameEnum.NA, BonusTimes.uxBonusEndNA){
			@Override
			public void run(String currentTime) {
				setUX(GameEnum.NA, false, currentTime);
			}
		});
		scheduler.addEvent(new Event("JP UX On", true, GameEnum.JP, BonusTimes.uxBonusStartJP){
			@Override
			public void run(String currentTime) {
				setUX(GameEnum.JP, true, currentTime);
			}
		});
		scheduler.addEvent(new Event("JP UX Off", true, GameEnum.JP, BonusTimes.uxBonusEndJP){
			@Override
			public void run(String currentTime) {
				setUX(GameEnum.JP, false, currentTime);
			}
		});
		scheduler.addTimedEvent(new TimedEvent("Twitter Update NA", true, 1) {
			@Override
			public void run() {
				twitterHandler.sendNewUpdates(serverConfigs, GameEnum.NA);
			}
		});
		scheduler.addTimedEvent(new TimedEvent("Twitter Update JP", true, 1) {
			@Override
			public void run() {
				twitterHandler.sendNewUpdates(serverConfigs, GameEnum.JP);
			}
		});
		scheduler.addTimedEvent(new TimedEvent("Bot Update", true, 20) {
			@Override
			public void run() {
				findUpdate();
			}
		});
		scheduler.addEvent(new Event("Daily", true, GameEnum.NA, "05:00:00"){
			@Override
			public void run(String currentTime) {
				keybladeHandler.updateKeybladeData();
				actionMessages.removeIf(a -> a.isExpired());
			}
		});
		scheduler.addTimedEvent(new TimedEvent("Reminders", true, 1) {
			@Override
			public void run() {
				int difNA = BonusTimes.luxTimeDifference(GameEnum.NA);
				int difJP = BonusTimes.luxTimeDifference(GameEnum.JP);
				if(difNA < 30 || difJP < 30) {
					for(Server server : api.getServers()) {
						ServerConfig config = getServerConfig(server);
						if(config.luxRemind>0) {
							if(!config.luxChannelNA.isEmpty()) {
								int luxTimeDifNA = BonusTimes.luxTimeDifference(GameEnum.NA, LuxCommand.getTimes(config, GameEnum.NA));
								if(config.luxRemind == luxTimeDifNA) {
									server.getTextChannelById(config.luxChannelNA).ifPresent(channel -> {
										channel.sendMessage("NA Reminder: Double lux in " + luxTimeDifNA + " minutes!");
									});
								}
							}
						
							if(!config.luxChannelJP.isEmpty()) {
								int luxTimeDifJP = BonusTimes.luxTimeDifference(GameEnum.JP, LuxCommand.getTimes(config, GameEnum.JP));
								if(config.luxRemind == luxTimeDifJP) {
									server.getTextChannelById(config.luxChannelJP).ifPresent(channel -> {
										channel.sendMessage("JP Reminder: Double lux in " + luxTimeDifJP + " minutes!");
									});
								}
							}
						}
					}
				}
				
				difNA = BonusTimes.uxTimeDifference(GameEnum.NA);
				difJP = BonusTimes.uxTimeDifference(GameEnum.JP);
				if(difNA < 30 || difJP < 30) {
					for(Server server : api.getServers()) {
						ServerConfig config = getServerConfig(server);
						if(config.ucRemind>0) {
							if(!config.ucChannelNA.isEmpty()) {
								int uxTimeDifNA = BonusTimes.uxTimeDifference(GameEnum.NA, UnionCrossCommand.getTimes(config, GameEnum.NA));
								if(config.ucRemind == uxTimeDifNA) {
									server.getTextChannelById(config.ucChannelNA).ifPresent(channel -> {
										channel.sendMessage("NA Reminder: Union Cross in " + uxTimeDifNA + " minutes!");
									});
								}
							}
						
							if(!config.ucChannelJP.isEmpty()) {
								int uxTimeDifJP = BonusTimes.uxTimeDifference(GameEnum.JP, UnionCrossCommand.getTimes(config, GameEnum.JP));
								if(config.ucRemind == uxTimeDifJP) {
									server.getTextChannelById(config.ucChannelJP).ifPresent(channel -> {
										channel.sendMessage("JP Reminder: Union Cross in " + uxTimeDifJP + " minutes!");
									});
								}
							}
						}
					}
				}
			}
		});
		System.out.println("Initialization finished!");
	}
	
	public void setLux(GameEnum game, boolean on, String currentTime) {
		for(ServerConfig config : serverConfigs){
			String[] times = on ? (game==GameEnum.NA ? BonusTimes.doubleLuxStartNA : BonusTimes.doubleLuxStartJP) : (game==GameEnum.NA ? BonusTimes.doubleLuxStopNA : BonusTimes.doubleLuxStopJP);
			if(BonusTimes.getTimes(LuxCommand.getTimes(config, game), times).contains(currentTime)) {
				String channel = game==GameEnum.NA ? config.luxChannelNA : config.luxChannelJP;
				if(!channel.isEmpty()){
					api.getTextChannelById(channel).ifPresent(c -> {
						c.sendMessage(game.toString() + ": " + (on ? config.luxOnPrompt : config.luxOffPrompt));
					});
				}
			}
		}
	}
	
	public void setUX(GameEnum game, boolean on, String currentTime) {
		for(ServerConfig config : serverConfigs){
			String[] times = on ? (game==GameEnum.NA ? BonusTimes.uxBonusStartNA : BonusTimes.uxBonusStartJP) : (game==GameEnum.NA ? BonusTimes.uxBonusStartNA : BonusTimes.uxBonusEndNA);
			if(BonusTimes.getTimes(UnionCrossCommand.getTimes(config, game), times).contains(currentTime)) {
				String channel = game==GameEnum.NA ? config.ucChannelNA : config.ucChannelJP;
				if(!channel.isEmpty()){
					api.getTextChannelById(channel).ifPresent(c -> {
						c.sendMessage(game.toString() + ": " + (on ? config.ucOnPrompt : config.ucOffPrompt));
					});
				}
			}
		}
	}
	
	/**
	 * Returns the ServerConfig class for the specified server.
	 * @param server to grab config for
	 * @return ServerConfig, or null if not registered.
	 */
	public static ServerConfig getServerConfig(Server server){
		String id = server.getIdAsString();
		for(ServerConfig config : serverConfigs){
			if(id.equals(config.serverId)){
				return config;
			}
		}
		return null;
	}
	
	/**
	 * Returns the ServerConfig class for the specified server.
	 * @param server to grab config for
	 * @return ServerConfig, or null if not registered.
	 */
	public static ServerConfig getServerConfig(User user){
		String id = user.getIdAsString();
		for(ServerConfig config : serverConfigs){
			if(id.equals(config.serverId)){
				return config;
			}
		}
		ServerConfig ret = new ServerConfig(user);
		serverConfigs.add(ret);
		return ret;
	}
	
	/**
	 * Registers all the commands
	 */
	public void registerCommands(){
		//Keep alphabetized in terms of first command alias. Because I'm too lazy to sort dynamically
		commandHandler.registerCommand(new AdminCommand());
		commandHandler.registerCommand(new ConfigCommand());
		commandHandler.registerCommand(new DefaultCommand());
		commandHandler.registerCommand(new HelpCommand());
		commandHandler.registerCommand(new KeybladeCommand());
		commandHandler.registerCommand(new LuxCommand());
		commandHandler.registerCommand(new MedalCommand());
		commandHandler.registerCommand(new MedalNACommand());
		commandHandler.registerCommand(new MedalJPCommand());
		commandHandler.registerCommand(new SaltCommand());
		commandHandler.registerCommand(new TweetCommand());
		commandHandler.registerCommand(new UnAdmin());
		commandHandler.registerCommand(new UnionCrossCommand());
	}

	/**
	 * Connects the client with the Discord servers, sets up listeners, and runs actions right after the servers connect.
	 * @param api
	 */
	public void connect(DiscordApi api) {
		for(Server server : api.getServers()){
			initializeServer(server);
		}
		api.addMessageCreateListener(event -> {
			Message message = event.getMessage();
			commandHandler.executeCommand(message);
			for(User u : message.getMentionedUsers()){
				if(u.isYourself()){
					Random rand = new Random();
					int i = rand.nextInt(COMEBACKS.length);
					message.getChannel().sendMessage(COMEBACKS[i]);
				}
			}
		});
		api.addReactionAddListener(event -> {
			event.getReaction().ifPresent(reaction -> {
				//Used for action messages, which are used for this like !medal and !lux
				if(reaction.getCount()>1) {
					for(ActionMessage am : actionMessages){
						if(am.isSameMessage(reaction.getMessage()) && am.test(ActionMessage.Type.ADD)) {
							am.run(reaction, ActionMessage.Type.ADD);
							if(am.killable) am.kill();
						}
					}
					actionMessages.removeIf(a -> a.dead);
				}
			});
		});
		api.addReactionRemoveListener(event -> {
			event.getReaction().ifPresent(reaction -> {
				for(ActionMessage am : actionMessages){
					if(am.isSameMessage(reaction.getMessage()) && am.test(ActionMessage.Type.REMOVE)) {
						am.run(reaction, ActionMessage.Type.REMOVE);
						if(am.killable) am.kill();
					}
				}
				actionMessages.removeIf(a -> a.dead);
			});
		});
		api.addServerJoinListener(event -> {
			Server server = event.getServer();
			System.out.println("Got new server " + server.getName() + ":" + server.getId());
			initializeServer(server);
		});
		api.addServerLeaveListener(event -> {
			Server server = event.getServer();
			synchronized(serverConfigs){
				System.out.println("Removing " + server.getName());
				//Config is removed from RAM, but still stored in case they come back :)
				serverConfigs.remove(getServerConfig(server));
			}
		});
		
		File userFiles = new File(ServerConfig.USER_DIR);
		if(userFiles != null && userFiles.listFiles() != null) {
			for(File file : userFiles.listFiles()){
				String id = file.getName().substring(0, file.getName().lastIndexOf("."));
				try {
					User user = api.getUserById(id).get();
					serverConfigs.add(new ServerConfig(user));
					user.sendMessage("");
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		
		scheduler.startThread();
		System.out.println("Connected to servers:");
		for(Server server : api.getServers()){
			System.out.println(">" + server.getName());
		}
		System.out.println("Total of " + api.getServers().size() + " servers connected.");
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
					for(ServerChannel channel : server.getTextChannels()) {
						if(channel.getName().equalsIgnoreCase("general")) {
							channelId = channel.getIdAsString();
						}
					}
				}
				if(!channelId.isEmpty()) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(Color.BLUE);
					eb.setTitle("Bot Update: " + VERSION);
					eb.setDescription(CHANGELOG);
					server.getTextChannelById(channelId).ifPresent(channel -> {
						channel.sendMessage(eb);
					});
				}
			}
		}
		api.updateActivity("Type !help for commands");
	}

	/**
	 * Searches my GitHub releases page to determine if there is an update available.
	 */
	public static void findUpdate() {
		//Yes, I know there's probably a much better, less hacky solution to this. But do I care? Only slightly.
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
