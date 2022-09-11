package tech.asmussen.auth.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import tech.asmussen.auth.core.UltraAuthenticator;
import tech.asmussen.auth.misc.QR;
import tech.asmussen.auth.util.Utility;
import tech.asmussen.util.Security;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class MainCommand extends Utility implements CommandExecutor {
	
	public void sendHelp(CommandSender sender) {
		
		String[] messages = Objects.requireNonNull(messageConfig.getString("all.commands.help"), "Message is null!").split("\n");
		
		for (String message : messages)
			
			determineAndSend(sender, message);
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		
		if (!sender.hasPermission(Objects.requireNonNull(permissionConfig.getString("use-permission"), "Permission is null!"))) {
			
			String noPermission = messageConfig.getString("all.no-permission");
			
			determineAndSend(sender, noPermission);
			
			return true;
		}
		
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			
			sendHelp(sender);
			
		} else if ("reload".equalsIgnoreCase(args[0])) {
			
			if (sender.hasPermission(Objects.requireNonNull(permissionConfig.getString("admin-permission"), "Permission is null!"))) {
				
				reloadConfigs();
				
				String pluginReloaded = messageConfig.getString("all.commands.reload");
				
				determineAndSend(sender, pluginReloaded);
				
			} else {
				
				String noPermission = messageConfig.getString("all.no-permission");
				
				determineAndSend(sender, noPermission);
			}
			
		} else if ("toggle".equalsIgnoreCase(args[0])) {
			
			if (!sender.hasPermission(Objects.requireNonNull(permissionConfig.getString("admin-permission"), "Permission is null!"))) {
				
				String noPermission = messageConfig.getString("all.no-permission");
				
				determineAndSend(sender, noPermission);
				
				return true;
			}
			
			if (args.length < 2) {
			
				String noPlayerSpecified = messageConfig.getString("all.no-player-specified");
				
				determineAndSend(sender, noPlayerSpecified);
				
			} else {
				
				Player target = Bukkit.getPlayer(args[1]);
				
				if (target == null) {
					
					String playerNotFound = Objects.requireNonNull(messageConfig.getString("all.player-not-found"), "Message is null!").replaceAll("%player%", args[1]);
					
					determineAndSend(sender, playerNotFound);
					
				} else {
					
					if (!userConfig.contains("users." + target.getUniqueId())) { // If the target doesn't have 2FA enabled, enable it.
						
						Security security = new Security();
						
						final String secretKey = security.generate2FA();
						
						UltraAuthenticator.AUTHENTICATION_LINKS.add(secretKey);
						
						QR.generateCode(secretKey, "otpauth://totp/" + target.getName() + "?secret=" + secretKey + "&issuer=Ultra%20Authenticator", 512);
						
						UltraAuthenticator.CURRENTLY_AUTHENTICATING.put(target.getUniqueId(), serverConfig.getInt("server.authentication.number-of-attempts"));
						
						// Update the config.
						userConfig.set("users." + target.getUniqueId() + ".secret", secretKey);
						userConfig.set("users." + target.getUniqueId() + ".verified", false);
						
						try {
							
							userConfig.save(new File(getPlugin().getDataFolder() + "/users.yml"));
							
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						
						String tfaEnabled = Objects.requireNonNull(messageConfig.getString("authentication.enabled"), "Message is null!").replaceAll("%player%", target.getName());
						String[] firstTimeMessages = new String[0];
						
						try {
							
							firstTimeMessages = Objects.requireNonNull(messageConfig.getString("authentication.first-time"), "Message is null!")
									.replaceAll("%player%", target.getName())
									.replaceAll("%link%", InetAddress.getLocalHost().getHostAddress() + ":" + UltraAuthenticator.webServerInstance.getPort() + "/" + secretKey)
									.split("\n");
							
						} catch (UnknownHostException e) {
							
							e.printStackTrace();
						}
						
						determineAndSend(sender, tfaEnabled);
						
						for (String message : firstTimeMessages)
							
							determineAndSend(target, message);
						
					} else { // Else, disable it.
						
						UltraAuthenticator.CURRENTLY_AUTHENTICATING.remove(target.getUniqueId());
						
						String secret = null;
						
						if (UltraAuthenticator.AUTHENTICATION_LINKS.contains(userConfig.getString("users." + target.getUniqueId() + ".secret"))) {
							
							secret = userConfig.getString("users." + target.getUniqueId() + ".secret");
							
							UltraAuthenticator.AUTHENTICATION_LINKS.remove(secret);
						}
						
						File qrCode = new File(getPlugin().getDataFolder() + "/QRs/" + secret + ".png");
						
						qrCode.delete();
						
						// Update the config.
						userConfig.set("users." + target.getUniqueId(), null);
						
						try {
							
							userConfig.save(new File(getPlugin().getDataFolder() + "/users.yml"));
							
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						
						String tfaDisabled = Objects.requireNonNull(messageConfig.getString("authentication.disabled"), "Message is null!").replaceAll("%player%", target.getName());
						
						determineAndSend(sender, tfaDisabled);
					}
				}
			}
			
		} else if ("version".equalsIgnoreCase(args[0])) {
			
			if (sender.hasPermission(Objects.requireNonNull(permissionConfig.getString("admin-permission"), "Permission is null!"))) {
				
				determineAndSend(sender, Objects.requireNonNull(messageConfig.getString("all.commands.version"), "Message is null!").replaceAll("%version%", UltraAuthenticator.VERSION));
				
			} else {
				
				String noPermission = messageConfig.getString("all.no-permission");
				
				determineAndSend(sender, noPermission);
			}
			
		} else {
			
			sendHelp(sender);
		}
		
		return true;
	}
}
