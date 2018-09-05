package xlash.bot.khux.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Handles the configuration for the bot as a whole.
 *
 */
public class BotConfig {
	
	public static final String FILE_NAME = System.getProperty("user.dir") + "/khuxbot config/config.properties";
	
	/**
	 * A unique and secret token id used for impersonating this program as a bot user.
	 */
	public String botToken;
	/**
	 * Used to determine if this bot instance is out of date.
	 */
	public String version;
	/**
	 * A global counter of how salty we all are
	 */
	public int saltCount;
	
	/**
	 * Stores information pertaining to the entire bot.
	 */
	public BotConfig(){
		init();
		File file = new File(FILE_NAME);
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
	
	/**
	 * Ensures that no variables are null
	 */
	public void init(){
		if(botToken == null) botToken = "";
		if(version == null) version = "";
	}
	
	/**
	 * Loads the config file into memory.
	 */
	public void loadConfig(){
		FileInputStream in;
		try {
			in = new FileInputStream(new File(FILE_NAME));
			Properties p = new Properties();
			p.load(in);
			this.botToken = p.getProperty("Bot_Token");
			this.version = p.getProperty("Version");
			String saltCountString = p.getProperty("Salt_Count");
			if(saltCountString != null) this.saltCount = Integer.parseInt(saltCountString);
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
		p.setProperty("Version", version);
		p.setProperty("Salt_Count", ""+this.saltCount);
		FileOutputStream os;
		try {
			os = new FileOutputStream(new File(FILE_NAME));
			p.store(os, "This is the config file for the KHUx Bot.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
