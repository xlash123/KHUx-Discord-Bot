package xlash.bot.khux.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import de.btobastian.javacord.entities.Server;
import xlash.bot.khux.GameEnum;

public class ServerConfig {
	
	public static final String DIRECTORY = System.getProperty("user.dir") + "/khuxbot config/";
	
	public volatile String updateChannel;
	public volatile String luxChannelNA;
	public volatile String luxChannelJP;
	public volatile GameEnum defaultGame;
	
	public volatile String luxOnPrompt;
	public volatile String luxOffPrompt;
	
	public final String serverId;
	
	public String botToken;
	
	public volatile ArrayList<String> admins;
	
	private ServerConfig(String fileName){
		this.serverId = fileName + ".properties";
		init();
		File file = new File(DIRECTORY);
		if(!file.exists()){
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				this.saveConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ServerConfig(Server server){
		this(server.getId());
	}
	
	public void init(){
		if(updateChannel == null) updateChannel = "";
		if(luxChannelNA == null) luxChannelNA = "";
		if(luxChannelJP == null) luxChannelJP = "";
		if(defaultGame == null) defaultGame = GameEnum.NA;
		if(luxOnPrompt == null) luxOnPrompt = "Double lux active!";
		if(luxOffPrompt == null) luxOffPrompt = "Double lux has faded...";
		if(admins == null) admins = new ArrayList<String>();
	}
	
	/**
	 * Loads the config file into memory.
	 */
	public void loadConfig(){
		FileInputStream in;
		try {
			in = new FileInputStream(new File(DIRECTORY + serverId));
			Properties p = new Properties();
			p.load(in);
			this.botToken = p.getProperty("Bot_Token");
			this.updateChannel = p.getProperty("Update_Channel");
			this.luxChannelNA = p.getProperty("Lux_Channel_NA");
			this.luxChannelJP = p.getProperty("Lux_Channel_JP");
			this.defaultGame = GameEnum.parseString(p.getProperty("Default_Game"));
			this.luxOnPrompt = p.getProperty("Lux_On_Prompt");
			this.luxOffPrompt = p.getProperty("Lux_Off_Prompt");
			String adminsString = p.getProperty("Bot_Admins");
			if(adminsString != null) this.admins.addAll(Arrays.asList(adminsString.split(",")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		init();
	}
	
	/**
	 * Saves the config file.
	 */
	public void saveConfig(){
		Properties p = new Properties();
		p.setProperty("Bot_Token", botToken);
		p.setProperty("Update_Channel", updateChannel);
		p.setProperty("Lux_Channel_NA", luxChannelNA);
		p.setProperty("Lux_Channel_JP", luxChannelJP);
		p.setProperty("Default_Game", defaultGame.toString());
		p.setProperty("Lux_On_Prompt", luxOnPrompt);
		p.setProperty("Lux_Off_Prompt", luxOffPrompt);
		String toSave = "";
		for(int i=0; i<admins.size(); i++){
			toSave += admins.get(i);
			if(i+1<admins.size()) toSave += ",";
		}
		p.setProperty("Bot_Admins", toSave);
		FileOutputStream os;
		try {
			os = new FileOutputStream(new File(DIRECTORY + serverId));
			p.store(os, "This is the config file for the Discord server of ID: " + serverId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
