package xlash.bot.khux.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import xlash.bot.khux.GameEnum;

public class Config {
	
	public static final String DIRECTORY = System.getProperty("user.dir") + "/khuxbot config/config.properties";
	
	public String botToken;
	public volatile String updateChannel;
	public volatile String luxChannel;
	public volatile GameEnum defaultGame;
	
	public volatile String luxOnPrompt;
	public volatile String luxOffPrompt;
	
	public Config(){
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
	
	public void init(){
		if(botToken == null) botToken = "";
		if(updateChannel == null) updateChannel = "";
		if(luxChannel == null) luxChannel = "";
		if(defaultGame == null) defaultGame = GameEnum.NA;
		if(luxOnPrompt == null) luxOnPrompt = "Double lux active!";
		if(luxOffPrompt == null) luxOffPrompt = "Double lux has faded...";
	}
	
	/**
	 * Loads the config file into memory.
	 */
	public void loadConfig(){
		FileInputStream in;
		try {
			in = new FileInputStream(new File(DIRECTORY));
			Properties p = new Properties();
			p.load(in);
			this.botToken = p.getProperty("Bot_Token");
			this.updateChannel = p.getProperty("Update_Channel");
			this.luxChannel = p.getProperty("Lux_Channel");
			this.defaultGame = GameEnum.parseString(p.getProperty("Default_Game"));
			this.luxOnPrompt = p.getProperty("Lux_On_Prompt");
			this.luxOffPrompt = p.getProperty("Lux_Off_Prompt");
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
		p.setProperty("Lux_Channel", luxChannel);
		p.setProperty("Default_Game", defaultGame.toString());
		p.setProperty("Lux_On_Prompt", luxOnPrompt);
		p.setProperty("Lux_Off_Prompt", luxOffPrompt);
		FileOutputStream os;
		try {
			os = new FileOutputStream(new File(DIRECTORY));
			p.store(os, "This is the config file for the KHUx Bot");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
