package xlash.bot.khux;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import de.btobastian.javacord.listener.server.ServerJoinListener;
import xlash.bot.khux.config.Config;

public class KHUxBot {
	
	public static final String VERSION = "1.1.3";
	
	public DiscordAPI api;

	public static MedalHandler medalHandler;
	public static TwitterHandler twitterHandler;
	public static Config config;
	
	public volatile boolean shouldUpdate;
	public volatile boolean shouldLux;

	public static void main(String[] args){
		if(args.length == 0){
		String runningFile;
		try {
			runningFile = new File(KHUxBot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
			ProcessBuilder builder = new ProcessBuilder(
					"cmd.exe", "/c", "start", "java", "-jar", "\"" + runningFile + "\"", "run");
			builder.redirectErrorStream(true);
			builder.start();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}else{
			findUpdate();
			config = new Config();
			config.loadConfig();
			if(config.botToken==null || config.botToken.isEmpty()){
				System.out.println("This is your first time running this bot. Thanks for installing!");
				System.out.println("To being using the bot, please enter your bot token.");
				System.out.println("If you need to make changes later, go to the config file in 'khuxbot config/config.properties'.");
				Scanner in = new Scanner(System.in);
				config.botToken = in.nextLine();
				in.close();
				config.saveConfig();
			}
			new KHUxBot();
		}
	}
	
	public KHUxBot(){
		this.initialize();
		api = Javacord.getApi(config.botToken, true);
		api.setAutoReconnect(false);
        connect(api);
        System.out.println("Waiting for server response...");
        while(api.getServers().size()==0){}
        System.out.println("Server connected! Let's go!");
        this.shouldUpdate = api.getChannelById(config.updateChannel)!=null;
        this.shouldLux = api.getChannelById(config.luxChannel)!=null;
        	Thread grabTwitterUpdate = new Thread("Grab Twitter Update"){
        		@Override
        		public void run(){
        			while(true){
        				try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
        				if(Integer.parseInt(getGMTTime("mm"))%2==0 && getGMTTime("ss").equals("05")){
        					twitterHandler.getTwitterUpdate(api);
        				}
        			}
        		}
        	};
        	grabTwitterUpdate.start();
        Thread botUpdate = new Thread("Bot Update"){
        	@Override
        	public void run(){
        		while(true){
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
        Thread luxTimes = new Thread("Lux Times"){
        	@Override
        	public void run(){
        		while(true){
        			if(shouldLux){
        				try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
        				if(getGMTTime("hh:mm:ss").equals("09:00:00")||getGMTTime("hh:mm:ss").equals("03:00:00")){
        					if(shouldLux) api.getChannelById(config.luxChannel).sendMessage("Double lux active!");
        				}else if(getGMTTime("hh:mm:ss").equals("10:00:00")||getGMTTime("hh:mm:ss").equals("04:00:00")){
        					if(shouldLux) api.getChannelById(config.luxChannel).sendMessage("Double lux has faded...");
        				}
        			}
        		}
        	}
        };
        luxTimes.start();
	}
	
	public void initialize(){
		System.out.println("Initializing...");
		medalHandler = new MedalHandler();
		twitterHandler = new TwitterHandler();
		System.out.println("Initialization finished!");
	}
	
	public void connect(DiscordAPI api){
		api.connect(new FutureCallback<DiscordAPI>() {
            public void onSuccess(DiscordAPI api) {
                api.registerListener(new MessageCreateListener() {
                    public void onMessageCreate(DiscordAPI api, Message message) {
                        if (message.getContent().startsWith("!medal ")) {
                            String medal = message.getContent().substring(7);
                            System.out.println("Medal in question: " + medal);
                            while(medalHandler.isDisabled()){}
                            String realName = medalHandler.getRealNameByNickname(medal);
                            System.out.println("Interpreted as: " + realName);
                            if(realName != null){
                            	medalHandler.getMedalInfo(realName, message);
                            }else{
                            	message.reply("I don't know what medal that is.");
                            }
                        }else if(message.getContent().startsWith("!tweet")){
                        	String param = message.getContent().substring(7);
                        	if(param.equalsIgnoreCase("on")){
                        		message.reply("Twitter updates are set to post on this channel.");
                        		config.updateChannel = message.getChannelReceiver().getId();
                        		shouldUpdate = true;
                        	}else if(param.equalsIgnoreCase("off")){
                        		message.reply("Twitter updates have been turned off.");
                        		config.updateChannel = "";
                        		shouldUpdate = false;
                        	}else if(param.equalsIgnoreCase("get")){
                        		message.reply(twitterHandler.getTwitterUpdateLink(0));
                        	}else{
                        		if(shouldLux)message.reply("Twitter update reminders are set for channel: #" + api.getChannelById(config.updateChannel).getName());
                        		else message.reply("Twitter updates are turned off.");
                        	}
                        }else if(message.getContent().startsWith("!lux")){
                        	String param = message.getContent().substring(5);
                        	if(param.equalsIgnoreCase("on")){
                        		message.reply("Double lux reminders are set to post on.");
                        		config.luxChannel = message.getChannelReceiver().getId();
                        		shouldLux = true;
                        	}else if(param.equalsIgnoreCase("off")){
                        		message.reply("Double lux reminders have been turned off.");
                        		config.luxChannel = "";
                        		shouldLux = false;
                        	}else{
                        		if(shouldLux) message.reply("Double lux reminders are set for channel: #" + api.getChannelById(config.luxChannel).getName());
                        		else message.reply("Double lux reminders are turned off.");
                        	}
                        }else if(message.getContent().equalsIgnoreCase("!refresh")){
                        	message.reply("Refreshing medal list. Please wait...");
                        	medalHandler.refreshMedalList();
                        	message.reply("Done! You may continue to query me.");
                        }else if(message.getContent().equalsIgnoreCase("!reset")){
                        	message.reply("Resetting medal descriptions. Please wait...");
                        	medalHandler.resetDescriptions();
                        	message.reply("Done! You may continue to query me.");
                        }
                    }
                });
            }

            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
	}
	
	public static void findUpdate(){
		try {
			Document doc = Jsoup.connect("https://github.com/xlash123/KHUx-Discord-Bot/releases").get();
			String newVersion = doc.getElementsByClass("css-truncate-target").get(0).text();
			if(!VERSION.equals(newVersion)){
				System.out.println("New update avaialbe. Download at - https://github.com/xlash123/KHUx-Discord-Bot/releases");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getGMTTime(String format){
		final Date currentTime = new Date();

		final SimpleDateFormat sdf =
		        new SimpleDateFormat(format);

		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(currentTime);
	}
	
	public static String getGMTTime(){
		return getGMTTime("HH:mm:ss");
	}
	
}
