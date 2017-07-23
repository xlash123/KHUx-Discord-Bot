package xlash.bot.khux.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import xlash.bot.khux.GameEnum;

public class BotConfig {
	
	public static final String FILE_NAME = System.getProperty("user.dir") + "/khuxbot config/config.properties";
	
	public String botToken;
	
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
	
	public void init(){
		if(botToken == null) botToken = "";
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
		FileOutputStream os;
		try {
			os = new FileOutputStream(new File(FILE_NAME));
			p.store(os, "This is the config file for the KHUx Bot");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
