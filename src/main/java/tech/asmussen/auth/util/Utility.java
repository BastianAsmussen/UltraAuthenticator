package tech.asmussen.auth.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tech.asmussen.auth.core.Config;
import tech.asmussen.auth.core.UltraAuthenticator;
import tech.asmussen.util.Security;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

public class Utility {
	
	private static final String PLUGIN_NAME = "UltraAuthenticator";
	
	public static FileConfiguration serverConfig;
	public static FileConfiguration permissionConfig;
	public static FileConfiguration messageConfig;
	public static FileConfiguration userConfig;
	
	public static Plugin getPlugin() {
		
		return Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
	}
	
	public static void validateVersion() {
		
		try {
			
			URL url = new URL("https://raw.githubusercontent.com/BastianAsmussen/UltraAuthenticator/main/VERSION");
			
			Scanner scanner = new Scanner(url.openStream());
			
			String latestVersion = scanner.nextLine();
			
			scanner.close();
			
			if (!latestVersion.equals(UltraAuthenticator.VERSION)) {
				
				consoleSend("&cYou are running an outdated version of UltraAuthenticator, please update to the latest version!");
				consoleSend("Your version: &c" + UltraAuthenticator.VERSION);
				consoleSend("Latest version: &a" + latestVersion);
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static String getPrefix() {
		
		String loadedPrefix = messageConfig.getString("all.prefix");
		
		return serverConfig.getBoolean("server.use-prefix") && loadedPrefix != null ? loadedPrefix : "";
	}
	
	public static String colorize(String text) {
		
		return text.replace("&", "§");
	}
	
	public static void determineAndSend(CommandSender sender, String message) {
		
		if (sender instanceof Player) {
			
			playerSend((Player) sender, message);
			
		} else {
			
			consoleSend(message);
		}
	}
	
	public static void consoleSend(String message) {
		
		if (message == null) throw new NullPointerException("Message cannot be null!");
		
		Bukkit.getConsoleSender().sendMessage(colorize(getPrefix() + message));
	}
	
	public static void playerSend(Player player, String message) {
		
		if (player == null) throw new NullPointerException("Player cannot be null!");
		if (message == null) throw new NullPointerException("Message cannot be null!");
		
		player.sendMessage(colorize(getPrefix() + message));
	}
	
	public static void reloadConfigs() {
		
		serverConfig = new Config("config.yml").getConfig();
		permissionConfig = new Config("permissions.yml").getConfig();
		messageConfig = new Config("messages.yml").getConfig();
		userConfig = new Config("users.yml").getConfig();
	}
	
	public static boolean isValidCode(Player player, int code) {
		
		final String secretKey = userConfig.getString("users." + player.getUniqueId() + ".secret");
		
		return new Security().validate2FA(Objects.requireNonNull(secretKey, "Secret is null!"), code);
	}
	
	public static void saveConfig(String configName) {
		
		try {
			switch (configName) {
				
				case "server" -> serverConfig.save(getPlugin().getDataFolder() + "/config.yml");
				case "permissions" -> permissionConfig.save(getPlugin().getDataFolder() + "/permissions.yml");
				case "messages" -> messageConfig.save(getPlugin().getDataFolder() + "/messages.yml");
				case "users" -> userConfig.save(getPlugin().getDataFolder() + "/users.yml");
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
