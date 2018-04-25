package xlash.bot.khux;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageReceiver;
import de.btobastian.javacord.entities.message.Reaction;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import de.btobastian.javacord.listener.message.ReactionAddListener;
import de.btobastian.javacord.listener.server.ServerJoinListener;
import de.btobastian.javacord.listener.server.ServerLeaveListener;
import de.btobastian.javacord.utils.ratelimits.RateLimitType;
import xlash.bot.khux.TwitterHandler.Tweet;
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

	public static final String VERSION = "1.7.1";

	/** Instance of the Discord API*/
	public static DiscordAPI api;

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
	 * Meant for sending many messages to many servers, checking rate limits along the way
	 */
	public static void sendMassive(String content, EmbedBuilder eb, MessageReceiver receiver) {
		Future<Message> mesF = receiver.sendMessage(content, eb);
		try {
			//Getting will halt the thread until the message is sent. Also, if I hit a rate limit, it'll throw, and I'll wait and send again.
			mesF.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			long rateLimit = 0;
			if(receiver instanceof Channel) {
				rateLimit = api.getRateLimitManager().getRateLimit(RateLimitType.SERVER_MESSAGE, null, (Channel) receiver);
			}else if(receiver instanceof User) {
				rateLimit = api.getRateLimitManager().getRateLimit(RateLimitType.PRIVATE_MESSAGE);
			}else System.err.println("Like what the heck are you doing?");
			if(rateLimit > 0) {
				System.out.println("Hit rate limit on \"" +content + "\". Waiting " + rateLimit + "ms.");
				try {
					Thread.sleep(rateLimit);
				} catch (InterruptedException ee) {
					e.printStackTrace();
				}
			}
			try {
				receiver.sendMessage(content, eb).get();
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
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
		scheduler.addEvent(new Event("NA Lux On", true, GameEnum.NA, BonusTimes.doubleLuxStartNA){
			@Override
			public void run() {
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.luxChannelNA.isEmpty()){
						Channel channel = server.getChannelById(config.luxChannelNA);
						if(channel != null){
							sendMassive("NA: " + config.luxOnPrompt, null, channel);
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
							sendMassive("NA: " + config.luxOffPrompt, null, channel);
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
							sendMassive("JP: " + config.luxOnPrompt, null, channel);
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
							sendMassive("JP: " + config.luxOffPrompt, null, channel);
						}
					}
				}
			}
		});
		scheduler.addEvent(new Event("NA UX On", true, GameEnum.NA, BonusTimes.uxBonusStartNA){
			@Override
			public void run() {
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.ucChannelNA.isEmpty()){
						Channel channel = server.getChannelById(config.ucChannelNA);
						if(channel != null){
							sendMassive("NA: " + config.ucOnPrompt, null, channel);
						}
					}
				}
			}
		});
		scheduler.addEvent(new Event("NA UX Off", true, GameEnum.NA, BonusTimes.uxBonusEndNA){
			@Override
			public void run() {
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.ucChannelNA.isEmpty()){
						Channel channel = server.getChannelById(config.ucChannelNA);
						if(channel != null){
							sendMassive("NA: " + config.ucOffPrompt, null, channel);
						}
					}
				}
			}
		});
		scheduler.addEvent(new Event("JP UX On", true, GameEnum.JP, BonusTimes.uxBonusStartJP){
			@Override
			public void run() {
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.ucChannelJP.isEmpty()){
						Channel channel = server.getChannelById(config.ucChannelJP);
						if(channel != null){
							sendMassive("JP: " + config.ucOnPrompt, null, channel);
						}
					}
				}
			}
		});
		scheduler.addEvent(new Event("JP UX Off", true, GameEnum.JP, BonusTimes.uxBonusEndJP){
			@Override
			public void run() {
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.ucChannelJP.isEmpty()){
						Channel channel = server.getChannelById(config.ucChannelJP);
						if(channel != null){
							sendMassive("JP: " + config.ucOffPrompt, null, channel);
						}
					}
				}
			}
		});
		scheduler.addTimedEvent(new TimedEvent("Twitter Update NA", true, 1) {
			@Override
			public void run() {
				ArrayList<Tweet> tweets = twitterHandler.getNewTwitterLinks(GameEnum.NA, true);
				if(tweets.isEmpty()) return;
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.updateChannelNA.isEmpty()){
						Channel channel = server.getChannelById(config.updateChannelNA);
						if(channel != null){
							for (Tweet tweet : tweets) {
								if (tweet != null)
									sendMassive(tweet.getLink(), null, channel);
							}
						}
					}
				}
			}
		});
		scheduler.addTimedEvent(new TimedEvent("Twitter Update JP", true, 1) {
			@Override
			public void run() {
				ArrayList<Tweet> tweets = twitterHandler.getNewTwitterLinks(GameEnum.JP, true);
				if(tweets.isEmpty()) return;
				for(Server server : api.getServers()){
					ServerConfig config = getServerConfig(server);
					if(!config.updateChannelNA.isEmpty()){
						Channel channel = server.getChannelById(config.updateChannelJP);
						if(channel != null){
							for (Tweet tweet : tweets) {
								if (tweet != null)
									sendMassive(tweet.getLink(), null, channel);
							}
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
		scheduler.addEvent(new Event("Daily", true, GameEnum.NA, "05:00:00"){
			@Override
			public void run() {
				actionMessages.removeIf(a -> a.isExpired());
				keybladeHandler.updateKeybladeData();
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
									Channel channel = api.getChannelById(config.luxChannelNA);
									if(channel != null) {
										sendMassive("NA Reminder: Double lux in " + luxTimeDifNA + " minutes!", null, channel);
									}
								}
							}
						
							if(!config.luxChannelJP.isEmpty()) {
								if(config.luxRemind == luxTimeDifJP) {
									Channel channel = api.getChannelById(config.luxChannelJP);
									if(channel != null) {
										sendMassive("JP Reminder: Double lux in " + luxTimeDifJP + " minutes!", null, channel);
									}
								}
							}
						}
					}
				}
				
				int uxTimeDifNA = BonusTimes.uxTimeDifference(GameEnum.NA);
				int uxTimeDifJP = BonusTimes.uxTimeDifference(GameEnum.JP);
				if(uxTimeDifNA < 30 || uxTimeDifJP < 30) {
					for(ServerConfig config : serverConfigs) {
						if(config.ucRemind>0) {
							if(!config.ucChannelNA.isEmpty()) {
								if(config.ucRemind == uxTimeDifNA) {
									Channel channel = api.getChannelById(config.ucChannelNA);
									if(channel != null) {
										sendMassive("NA Reminder: Union Cross in " + uxTimeDifNA + " minutes!", null, channel);
									}
								}
							}
						
							if(!config.ucChannelJP.isEmpty()) {
								if(config.ucRemind == uxTimeDifJP) {
									Channel channel = api.getChannelById(config.ucChannelJP);
									if(channel != null) {
										sendMassive("JP Reminder: Union Cross in " + uxTimeDifJP + " minutes!", null, channel);
									}
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
	
	/**
	 * Registers all the commands
	 */
	public void registerCommands(){
		//Keep alphabetized in terms of command usage
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
				
				api.registerListener(new ReactionAddListener() {
					
					@Override
					public void onReactionAdd(DiscordAPI api, Reaction reaction, User user) {
						if(reaction.getCount()>1) {
							for(ActionMessage am : actionMessages){
								if(am.isSameMessage(reaction.getMessage())) {
									am.run(reaction);
									am.kill();
								}
							}
							actionMessages.removeIf(a -> a.dead);
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
							for(Channel channel : server.getChannels()) {
								if(channel.getName().equals("general")) {
									channelId = channel.getId();
								}
							}
						}
						if(!channelId.isEmpty()) {
							EmbedBuilder eb = new EmbedBuilder();
							eb.setColor(Color.BLUE);
							eb.setTitle("Bot Update: " + VERSION);
							eb.setDescription("Potentially fixed issues with some servers not receiving timed notifications.");
							sendMassive("", eb, server.getChannelById(channelId));
						}
					}
				}
				api.setGame("Type !help for commands");
			}

			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});
	}

	/**
	 * Searches my GitHub releases page to determine if there is an update available.
	 */
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
