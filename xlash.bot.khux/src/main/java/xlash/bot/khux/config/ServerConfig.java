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
import xlash.bot.khux.KHUxBot;

/**
 * Handles the configuration for each individual connected server
 *
 */
public class ServerConfig {
	
	public static final String DIRECTORY = System.getProperty("user.dir") + "/khuxbot config/";
	public final String fileName;
	
	public volatile String updateChannelNA;
	public volatile String updateChannelJP;
	public volatile String luxChannelNA;
	public volatile String luxChannelJP;
	public volatile String raidChannelNA;
	public volatile String raidChannelJP;
	public volatile String uxChannelNA;
	public volatile String uxChannelJP;
	public volatile GameEnum defaultGame;
	
	public volatile String luxOnPrompt;
	public volatile String luxOffPrompt;
	public volatile int luxRemind;
	public volatile int raidRemind;
	public volatile int uxRemind;
	
	public final String serverId;
	
	public String botToken;
	
	public volatile ArrayList<String> admins;
	
	private ServerConfig(String serverId){
		this.serverId = serverId;
		this.fileName = DIRECTORY + serverId + ".properties";
		init();
		File file = new File(DIRECTORY + serverId + ".properties");
		if(!file.exists()){
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				this.saveConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			this.loadConfig();
		}
	}
	
	/**
	 * Creates a blank instance used for handling DMs
	 */
	private ServerConfig() {
		fileName = "";
		serverId = "";
		init();
	}
	
	/**
	 * Initializes a ServerConfig for the server of specified id
	 * @param serverId id of the server
	 */
	public ServerConfig(Server server){
		this(server.getId());
	}
	
	public void init(){
		if(updateChannelNA == null) updateChannelNA = "";
		if(updateChannelJP == null) updateChannelJP = "";
		if(luxChannelNA == null) luxChannelNA = "";
		if(luxChannelJP == null) luxChannelJP = "";
		if(uxChannelNA == null) uxChannelNA = "";
		if(uxChannelJP == null) uxChannelJP = "";
		if(raidChannelNA == null) raidChannelNA = "";
		if(raidChannelJP == null) raidChannelJP = "";
		if(defaultGame == null) defaultGame = GameEnum.NA;
		if(luxOnPrompt == null) luxOnPrompt = "Double lux active!";
		if(luxOffPrompt == null) luxOffPrompt = "Double lux has faded...";
		if(admins == null) admins = new ArrayList<String>();
	}
	
	public static ServerConfig getBlank() {
		return new ServerConfig();
	}
	
	/**
	 * Loads the config file into memory.
	 */
	public void loadConfig(){
		FileInputStream in;
		try {
			in = new FileInputStream(new File(fileName));
			Properties p = new Properties();
			p.load(in);
			this.botToken = p.getProperty("Bot_Token");
			this.updateChannelNA = p.getProperty("Update_Channel_NA");
			this.updateChannelJP = p.getProperty("Update_Channel_JP");
			this.luxChannelNA = p.getProperty("Lux_Channel_NA");
			this.luxChannelJP = p.getProperty("Lux_Channel_JP");
			this.defaultGame = GameEnum.parseString(p.getProperty("Default_Game"));
			this.luxOnPrompt = p.getProperty("Lux_On_Prompt");
			this.luxOffPrompt = p.getProperty("Lux_Off_Prompt");
			String adminsString = p.getProperty("Bot_Admins");
			if(adminsString != null) this.admins.addAll(Arrays.asList(adminsString.split(",")));
			String luxRemindString = p.getProperty("Lux_Remind");
			if(luxRemindString != null) this.luxRemind = Integer.parseInt(luxRemindString);
			this.uxChannelNA = p.getProperty("UX_Channel_NA");
			this.uxChannelJP = p.getProperty("UX_Channel_JP");
			this.raidChannelNA = p.getProperty("Raid_Channel_NA");
			this.raidChannelJP = p.getProperty("Raid_Channel_JP");
			String uxRemindString = p.getProperty("UX_Remind");
			if(uxRemindString != null) this.uxRemind = Integer.parseInt(uxRemindString);
			String raidRemindString = p.getProperty("Raid_Remind");
			if(raidRemindString != null) this.raidRemind = Integer.parseInt(raidRemindString);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		init();
	}
	
	/**
	 * Saves the config file.
	 */
	public void saveConfig(){
		init();
		Properties p = new Properties();
		p.setProperty("Update_Channel_NA", updateChannelNA);
		p.setProperty("Update_Channel_JP", updateChannelJP);
		p.setProperty("Lux_Channel_NA", luxChannelNA);
		p.setProperty("Lux_Channel_JP", luxChannelJP);
		p.setProperty("Default_Game", defaultGame.toString());
		p.setProperty("Lux_On_Prompt", luxOnPrompt);
		p.setProperty("Lux_Off_Prompt", luxOffPrompt);
		p.setProperty("Lux_Remind", ""+luxRemind);
		p.setProperty("UX_Channel_NA", uxChannelNA);
		p.setProperty("UX_Channel_JP", uxChannelJP);
		p.setProperty("Raid_Channel_NA", raidChannelNA);
		p.setProperty("Raid_Channel_JP", raidChannelJP);
		p.setProperty("UX_Remind", ""+uxRemind);
		p.setProperty("Raid_Remind", ""+raidRemind);
		String toSave = "";
		for(int i=0; i<admins.size(); i++){
			toSave += admins.get(i);
			if(i+1<admins.size()) toSave += ",";
		}
		p.setProperty("Bot_Admins", toSave);
		FileOutputStream os;
		try {
			os = new FileOutputStream(new File(fileName));
			p.store(os, "This is the config file for the Discord server: " + KHUxBot.api.getServerById(serverId).getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
