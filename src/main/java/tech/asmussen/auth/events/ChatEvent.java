package tech.asmussen.auth.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import tech.asmussen.auth.core.UltraAuthenticator;
import tech.asmussen.auth.util.Utility;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ChatEvent extends Utility implements Listener {
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		
		if (UltraAuthenticator.CURRENTLY_AUTHENTICATING.get(event.getPlayer().getUniqueId()) == null) return;
		
		Player player = event.getPlayer();
		
		String successMessage = Objects.requireNonNull(messageConfig.getString("authentication.login.success"), "Message is null!").replaceAll("%player%", player.getName());
		String failureMessage = Objects.requireNonNull(messageConfig.getString("authentication.login.failure"), "Message is null!");
		
		try {
			
			int code = Integer.parseInt(event.getMessage());
			
			if (isValidCode(event.getPlayer(), code)) {
				
				UltraAuthenticator.CURRENTLY_AUTHENTICATING.remove(player.getUniqueId());
				
				// Update the config.
				userConfig.set("users." + player.getUniqueId() + ".verified", true);
				
				try {
					
					userConfig.save(new File(getPlugin().getDataFolder() + "/users.yml"));
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
				playerSend(player, successMessage);
				
			} else {
				
				UltraAuthenticator.CURRENTLY_AUTHENTICATING.put(player.getUniqueId(), UltraAuthenticator.CURRENTLY_AUTHENTICATING.get(player.getUniqueId()) - 1);
				
				if (UltraAuthenticator.CURRENTLY_AUTHENTICATING.get(player.getUniqueId()) < 1) {
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(
							getPlugin(),
							() -> {
								
								player.kickPlayer(colorize(failureMessage.replaceAll("%attempts%" , UltraAuthenticator.CURRENTLY_AUTHENTICATING.get(player.getUniqueId()).toString())));
								
								UltraAuthenticator.CURRENTLY_AUTHENTICATING.remove(player.getUniqueId());
							},
							1L
					);
					
				} else {
					
					playerSend(player, failureMessage.replaceAll("%attempts%" , UltraAuthenticator.CURRENTLY_AUTHENTICATING.get(player.getUniqueId()).toString()));
				}
			}
			
		} catch (NumberFormatException e) {
			
			playerSend(player, failureMessage.replaceAll("%attempts%" , UltraAuthenticator.CURRENTLY_AUTHENTICATING.get(player.getUniqueId()).toString()));
		}
		
		event.setCancelled(true);
	}
}
