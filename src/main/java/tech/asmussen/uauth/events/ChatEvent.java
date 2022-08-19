package tech.asmussen.uauth.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import tech.asmussen.uauth.UltraAuthenticator;

import java.util.Objects;

public class ChatEvent implements Listener {
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		
		if (!UltraAuthenticator.currentlyVerifying.contains(event.getPlayer().getUniqueId())) return;
		
		Player player = event.getPlayer();
		
		String successMessage = Objects.requireNonNull(UltraAuthenticator.getPlugin().getConfig().getString("messages.user.login.success"), "Success message is null!").replaceAll("%p", player.getName());
		String failureMessage = Objects.requireNonNull(UltraAuthenticator.getPlugin().getConfig().getString("messages.user.login.failure"), "Failure message is null!").replaceAll("%p", player.getName());
		
		try {
			
			int code = Integer.parseInt(event.getMessage());
			
			if (UltraAuthenticator.isValidCode(event.getPlayer(), code)) {
				
				UltraAuthenticator.currentlyVerifying.remove(player.getUniqueId());
				
				UltraAuthenticator.getPlugin().getConfig().set("2fa." + player.getUniqueId() + ".is-verified", true);
				UltraAuthenticator.getPlugin().saveConfig();
				
				UltraAuthenticator.playerSend(player, successMessage);
				
			} else {
				
				UltraAuthenticator.playerSend(player, failureMessage);
			}
			
		} catch (NumberFormatException e) {
			
			UltraAuthenticator.playerSend(player, failureMessage);
		}
		
		event.setCancelled(true);
	}
}
