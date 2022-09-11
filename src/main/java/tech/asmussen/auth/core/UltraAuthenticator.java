package tech.asmussen.auth.core;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import tech.asmussen.auth.commands.*;
import tech.asmussen.auth.events.*;
import tech.asmussen.auth.util.Utility;
import tech.asmussen.auth.web.WebServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class UltraAuthenticator extends JavaPlugin {
	
	public static final String VERSION = "2.0.0";
	
	public static final HashMap<UUID, Integer> CURRENTLY_AUTHENTICATING = new HashMap<>();
	public static final ArrayList<String> AUTHENTICATION_LINKS = new ArrayList<>();
	
	public static WebServer webServerInstance;
	
	@Override
	public void onEnable() {
		
		Plugin plugin = Utility.getPlugin();
		
		// Load the configurations.
		Utility.reloadConfigs();
		
		// Verify the version of the plugin.
		Utility.validateVersion();
		
		// Register the commands.
		Utility.consoleSend("Registering commands...");
		
		Objects.requireNonNull(this.getCommand("uauth"), "Command is null!").setExecutor(new MainCommand());
		
		// Register the events.
		Utility.consoleSend("Registering events...");
		
		plugin.getServer().getPluginManager().registerEvents(new ChatEvent(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new JoinEvent(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new QuitEvent(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new MoveEvent(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new InteractEvent(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new DamageEvent(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new CommandEvent(), plugin);
		
		// Start the web server.
		Utility.consoleSend("Starting web server...");
		
		WebServer.makeWebFiles(plugin.getDataFolder() + Utility.serverConfig.getString("server.web.path"));
		
		webServerInstance = new WebServer(Utility.serverConfig.getInt("server.web.port"));
		new Thread(() -> webServerInstance.run()).start();
		
		// Notify the console that the plugin is enabled.
		Utility.consoleSend(Utility.messageConfig.getString("console.plugin.enabled"));
	}
	
	@Override
	public void onDisable() {
		
		// Notify the console that the plugin is disabled.
		Utility.consoleSend(Utility.messageConfig.getString("console.plugin.disabled"));
	}
}
