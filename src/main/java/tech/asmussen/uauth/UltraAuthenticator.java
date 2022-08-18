package tech.asmussen.uauth;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import tech.asmussen.uauth.events.Events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public final class UltraAuthenticator extends JavaPlugin {
	
	private FileConfiguration authConfig;
	
	public ArrayList<UUID> authLocked;
	
	@Override
	public void onEnable() {
		
		createAuthConfig();
		
		// Register all events
		this.getServer().getPluginManager().registerEvents(new Events(), this);
		
		authLocked = new ArrayList<>();
		
		// Load configuration
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}
	
	@Override
	public void onDisable() {
	
	}
	
	public FileConfiguration getAuthConfig() {
		
		return this.authConfig;
	}
	
	private void createAuthConfig() {
		
		File authFile = new File(getDataFolder(), "auth.yml");
		
		if (!authFile.exists()) {
			
			if (authFile.getParentFile().mkdirs()) {
				
				Bukkit.getLogger().log(Level.INFO, "Created authentication configuration!");
			}
			
			saveResource("auth.yml", false);
		}
		
		authConfig = new YamlConfiguration();
		
		try {
			
			authConfig.load(authFile);
			
		} catch (IOException | InvalidConfigurationException e) {
			
			e.printStackTrace();
		}
	}
}
