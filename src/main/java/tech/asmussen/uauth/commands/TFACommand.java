package tech.asmussen.uauth.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tech.asmussen.uauth.UltraAuthenticator;
import tech.asmussen.util.Security;

import java.util.Objects;

public class TFACommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		
		if (!sender.hasPermission(Objects.requireNonNull(UltraAuthenticator.getPlugin().getConfig().getString("settings.admin-permission"), "Admin permission is null!"))) {
			
			UltraAuthenticator.playerSend((Player) sender, Objects.requireNonNull(UltraAuthenticator.getPlugin().getConfig().getString("messages.user.no-permission"), "No permission message is null!").replaceAll("%p", sender.getName()));
			
			return true;
		}
		
		String playerNotFound = UltraAuthenticator.getPlugin().getConfig().getString("messages.user.player-not-found");
		
		String tfaEnabled = UltraAuthenticator.getPlugin().getConfig().getString("messages.user.2fa.enabled");
		String tfaDisabled = UltraAuthenticator.getPlugin().getConfig().getString("messages.user.2fa.disabled");
		String tfaMessage = UltraAuthenticator.getPlugin().getConfig().getString("messages.user.2fa.message");
		String tfaSecret = UltraAuthenticator.getPlugin().getConfig().getString("messages.user.2fa.secret");
		
		if (args.length == 0) return false;
		else {
			
			Player player = Bukkit.getPlayer(args[0]);
			
			if (player != null && player.isOnline()) {
				
				Security security = new Security();
				
				assert tfaEnabled != null;
				assert tfaDisabled != null;
				assert tfaMessage != null;
				assert tfaSecret != null;
				
				final String secretKey = security.generate2FA();
				
				tfaEnabled = tfaEnabled.replaceAll("%p", player.getName());
				tfaDisabled = tfaDisabled.replaceAll("%p", player.getName());
				tfaMessage = tfaMessage.replaceAll("%p", player.getName());
				tfaSecret = tfaSecret.replaceAll("%s", secretKey);
				
				if (!UltraAuthenticator.getPlugin().getConfig().contains("2fa." + player.getUniqueId())) {
					
					UltraAuthenticator.currentlyVerifying.add(player.getUniqueId());
					UltraAuthenticator.getPlugin().getConfig().set("2fa." + player.getUniqueId() + ".key", secretKey);
					UltraAuthenticator.getPlugin().getConfig().set("2fa." + player.getUniqueId() + ".is-verified", false);
					UltraAuthenticator.getPlugin().saveConfig();
					
					// Send the message to the command sender.
					determineType(sender,tfaEnabled);
					
					UltraAuthenticator.playerSend(player, tfaSecret);
					UltraAuthenticator.playerSend(player, tfaMessage);
					
				} else {
					
					UltraAuthenticator.currentlyVerifying.remove(player.getUniqueId());
					UltraAuthenticator.getPlugin().getConfig().set("2fa." + player.getUniqueId(), null);
					UltraAuthenticator.getPlugin().saveConfig();
					
					// Send the message to the command sender.
					determineType(sender, tfaDisabled);
				}
				
			} else {
				
				assert playerNotFound != null;
				
				// Send the message to the command sender.
				determineType(sender, playerNotFound.replaceAll("%p", args[0]));
			}
			
			return true;
		}
	}
	
	public static void determineType(CommandSender sender, String message) {
		
		if (!(sender instanceof Player)) // If the sender is Console.
			
			UltraAuthenticator.consoleSend(message);
		
		else // Else, the sender must be a player.
			
			UltraAuthenticator.playerSend((Player) sender, message);
	}
}
