package tech.asmussen.uauth;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tech.asmussen.uauth.commands.TFACommand;
import tech.asmussen.uauth.events.*;
import tech.asmussen.util.Security;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public final class UltraAuthenticator extends JavaPlugin {
	
	public static ArrayList<UUID> currentlyVerifying = new ArrayList<>();
	
	public static Plugin getPlugin() {
		
		return getPlugin(UltraAuthenticator.class);
	}
	
	/**
	 * Gets the prefix from the config.yml.
	 * If the prefix is null, it will return the default prefix.
	 * If the use-prefix parameter is false, it will return an empty string.
	 *
	 * @return The prefix.
	 */
	public static String getPrefix() {
		
		final String defaultPrefix = "&8[&b&luAuth&8]&7: &r";
		
		String loadedPrefix = getPlugin().getConfig().getString("messages.server.prefix");
		
		return !getPlugin().getConfig().getBoolean("settings.use-prefix") ? "" : loadedPrefix == null ? defaultPrefix : loadedPrefix;
	}
	
	public static void consoleSend(String message) {
		
		getPlugin().getServer().getConsoleSender().sendMessage(colorize(getPrefix() + message));
	}
	
	public static void playerSend(Player player, String message) {
		
		player.sendMessage(colorize(getPrefix() + message));
	}
	
	public static String colorize(String message) {
		
		return message.replace("&", "§");
	}
	
	public static boolean isValidCode(Player player, int code) {
		
		String secretKey = Objects.requireNonNull(getPlugin().getConfig().getString("2fa." + player.getUniqueId() + ".key"), "Secret key is null!");
		
		return new Security().validate2FA(secretKey, code);
	}
	
	@Override
	public void onEnable() {
		
		// Load default configuration.
		getPlugin().saveDefaultConfig();
		
		// Register commands.
		Objects.requireNonNull(this.getCommand("uauth"), "Command is null!").setExecutor(new TFACommand());
		
		// Register events.
		getPlugin().getServer().getPluginManager().registerEvents(new JoinEvent(), getPlugin());
		getPlugin().getServer().getPluginManager().registerEvents(new QuitEvent(), getPlugin());
		getPlugin().getServer().getPluginManager().registerEvents(new ChatEvent(), getPlugin());
		getPlugin().getServer().getPluginManager().registerEvents(new MoveEvent(), getPlugin());
		getPlugin().getServer().getPluginManager().registerEvents(new InteractEvent(), getPlugin());
		
		consoleSend(getConfig().getString("messages.server.plugin.enabled"));
	}
	
	@Override
	public void onDisable() {
		
		consoleSend(getConfig().getString("messages.server.plugin.disabled"));
	}
}
