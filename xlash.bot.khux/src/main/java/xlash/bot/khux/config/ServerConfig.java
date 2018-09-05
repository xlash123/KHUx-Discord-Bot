package xlash.bot.khux.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;

/**
 * Handles the configuration for each individual connected server
 *
 */
public class ServerConfig {
	
	public static final String DIRECTORY = System.getProperty("user.dir") + "/khuxbot config/";
	public static final String USER_DIR = DIRECTORY + "users/";
	public final String fileName;
	
	public volatile String updateChannelNA, updateChannelJP;
	public volatile String luxChannelNA, luxChannelJP;
	public volatile String raidChannelNA, raidChannelJP;
	public volatile String ucChannelNA, ucChannelJP;
	public volatile GameEnum defaultGame;
	public volatile int luxSelectionsNA, luxSelectionsJP;
	public volatile int ucSelectionsNA, ucSelectionsJP;
	
	public volatile String luxOnPrompt, luxOffPrompt;
	public volatile String ucOnPrompt, ucOffPrompt;
	public volatile int luxRemind;
	public volatile int raidRemind;
	public volatile int ucRemind;
	
	public final String serverId;
	
	public boolean isUser;
	
	public volatile ArrayList<String> admins;
	
	private ServerConfig(String serverId, boolean user){
		this.isUser = user;
		this.serverId = serverId;
		this.fileName = (user ? USER_DIR : DIRECTORY) + serverId + ".properties";
		init();
		File file = new File(fileName);
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
		this(server.getIdAsString(), false);
	}
	
	public ServerConfig(User user) {
		this(user.getIdAsString(), true);
	}
	
	/**
	 * Ensures that no variable is null
	 */
	public void init(){
		if(updateChannelNA == null) updateChannelNA = "";
		if(updateChannelJP == null) updateChannelJP = "";
		if(luxChannelNA == null) luxChannelNA = "";
		if(luxChannelJP == null) luxChannelJP = "";
		if(ucChannelNA == null) ucChannelNA = "";
		if(ucChannelJP == null) ucChannelJP = "";
		if(raidChannelNA == null) raidChannelNA = "";
		if(raidChannelJP == null) raidChannelJP = "";
		if(defaultGame == null) defaultGame = GameEnum.NA;
		if(luxOnPrompt == null) luxOnPrompt = "Double lux active!";
		if(luxOffPrompt == null) luxOffPrompt = "Double lux has faded...";
		if(ucOnPrompt == null) ucOnPrompt = "Union Cross bonus time active!";
		if(ucOffPrompt == null) ucOffPrompt = "Union Cross bonus time has finished.";
		if(admins == null) admins = new ArrayList<String>();
	}
	
	/**
	 * Used for when dealing with DMs
	 * @return
	 */
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
			this.updateChannelNA = p.getProperty("Update_Channel_NA");
			this.updateChannelJP = p.getProperty("Update_Channel_JP");
			this.luxChannelNA = p.getProperty("Lux_Channel_NA");
			this.luxChannelJP = p.getProperty("Lux_Channel_JP");
			this.defaultGame = GameEnum.parseString(p.getProperty("Default_Game"));
			this.luxOnPrompt = p.getProperty("Lux_On_Prompt");
			this.luxOffPrompt = p.getProperty("Lux_Off_Prompt");
			this.ucOnPrompt = p.getProperty("UC_On_Prompt");
			this.ucOffPrompt = p.getProperty("UC_Off_Prompt");
			String adminsString = p.getProperty("Bot_Admins");
			if(adminsString != null) this.admins.addAll(Arrays.asList(adminsString.split(",")));
			this.luxRemind = stringToInt(p.getProperty("Lux_Remind"));
			this.ucChannelNA = p.getProperty("UC_Channel_NA");
			this.ucChannelJP = p.getProperty("UC_Channel_JP");
			this.raidChannelNA = p.getProperty("Raid_Channel_NA");
			this.raidChannelJP = p.getProperty("Raid_Channel_JP");
			this.ucRemind = stringToInt(p.getProperty("UC_Remind"));
			this.raidRemind = stringToInt(p.getProperty("Raid_Remind"));
			this.luxSelectionsNA = stringToInt(p.getProperty("Lux_Selections_NA"));
			this.luxSelectionsJP = stringToInt(p.getProperty("Lux_Selections_JP"));
			this.ucSelectionsNA = stringToInt(p.getProperty("UC_Selections_NA"));
			this.ucSelectionsJP = stringToInt(p.getProperty("UC_Selections_JP"));
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
	 * Easy way to safely convert Strings to integers
	 * @param field
	 * @return Integer.parseInt() or 0 if null or on exception
	 */
	public int stringToInt(String field) {
		try {
			if(field != null) {
				return Integer.parseInt(field);
			}
		}catch (NumberFormatException e) {
		}
		return 0;
	}
	
	/**
	 * Gets the data stored in the specified field on the written copy of the config.
	 * @param prop
	 * @return
	 */
	public String getConfig(String prop) { 
		FileInputStream in;
		try {
			in = new FileInputStream(new File(fileName));
			Properties p = new Properties();
			p.load(in);
			return p.getProperty(prop, "null");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "null";
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
		p.setProperty("UC_On_Prompt", ucOnPrompt);
		p.setProperty("UC_Off_Prompt", ucOffPrompt);
		p.setProperty("Lux_Remind", ""+luxRemind);
		p.setProperty("UC_Channel_NA", ucChannelNA);
		p.setProperty("UC_Channel_JP", ucChannelJP);
		p.setProperty("Raid_Channel_NA", raidChannelNA);
		p.setProperty("Raid_Channel_JP", raidChannelJP);
		p.setProperty("UC_Remind", ""+ucRemind);
		p.setProperty("Raid_Remind", ""+raidRemind);
		//Admins saved in comma separated list
		String toSave = "";
		for(int i=0; i<admins.size(); i++){
			toSave += admins.get(i)+",";
		}
		if(toSave.length()>0) toSave = toSave.substring(0, toSave.length()-1);
		p.setProperty("Bot_Admins", toSave);
		//End of admin saving
		p.setProperty("Lux_Selections_NA", ""+luxSelectionsNA);
		p.setProperty("Lux_Selections_JP", ""+luxSelectionsJP);
		p.setProperty("UC_Selections_NA", ""+ucSelectionsNA);
		p.setProperty("UC_Selections_JP", ""+ucSelectionsJP);
			
		if(isUser) {
			try {
				FileOutputStream os = new FileOutputStream(new File(fileName));
				p.store(os, "This is the config file for the user: " + KHUxBot.api.getUserById(serverId).get().getDiscriminatedName());
			} catch (IOException | InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}else{
			KHUxBot.api.getServerById(serverId).ifPresent(server -> {
				try {
					FileOutputStream os = new FileOutputStream(new File(fileName));
					p.store(os, "This is the config file for the Discord server: " + server.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	/**
	 * Puts the specified value into the specified field and saves it to the written copy of the config
	 * @param prop
	 * @param value
	 */
	public void putConfig(String prop, String value) {
		if(isUser) {
			try {
				FileInputStream in = new FileInputStream(new File(fileName));
				Properties p = new Properties();
				p.load(in);
				p.setProperty(prop, value);
				p.store(new FileOutputStream(fileName), "This is the config file for the Discord server: " + KHUxBot.api.getUserById(serverId).get().getDiscriminatedName());
			} catch (IOException | InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}else{
			KHUxBot.api.getServerById(serverId).ifPresent(server -> {
				try {
					FileInputStream in = new FileInputStream(new File(fileName));
					Properties p = new Properties();
					p.load(in);
					p.setProperty(prop, value);
					p.store(new FileOutputStream(fileName), "This is the config file for the Discord server: " + server.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		this.loadConfig();
	}

}
