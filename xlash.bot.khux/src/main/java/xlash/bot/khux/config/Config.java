package xlash.bot.khux.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class Config {
	
	public static final String DIRECTORY = System.getProperty("user.dir") + "/khuxbot config/config.properties";
	
	public String botToken;
	public volatile String updateChannel;
	public volatile String luxChannel;
	
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
	}
	
	public void loadConfig(){
		FileInputStream in;
		try {
			in = new FileInputStream(new File(DIRECTORY));
			Properties p = new Properties();
			p.load(in);
			this.botToken = p.getProperty("Bot_Token");
			this.updateChannel = p.getProperty("Update_Channel");
			this.luxChannel = p.getProperty("Lux_Channel");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		init();
	}
	
	public void saveConfig(){
		Properties p = new Properties();
		p.setProperty("Bot_Token", botToken);
		p.setProperty("Update_Channel", updateChannel);
		p.setProperty("Lux_Channel", luxChannel);
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
