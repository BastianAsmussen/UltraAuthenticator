package tech.asmussen.auth.core;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import tech.asmussen.auth.util.Utility;

import java.io.File;
import java.io.IOException;

public final class Config extends Utility {
	
	private final FileConfiguration config;
	
	public Config(String file) {
		
		Plugin plugin = getPlugin();
		
		File configFile = new File(plugin.getDataFolder(), file);
		
		if (!configFile.exists())
			
			plugin.saveResource(file, false);
		
		config = new YamlConfiguration();
		
		try {
			
			config.load(configFile);
			
		} catch (IOException | InvalidConfigurationException e) {
			
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getConfig() {
		
		return this.config;
	}
}
