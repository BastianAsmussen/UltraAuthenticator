package tech.asmussen.auth.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import tech.asmussen.auth.core.UltraAuthenticator;

public class QuitEvent implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		
		UltraAuthenticator.CURRENTLY_AUTHENTICATING.remove(event.getPlayer().getUniqueId());
	}
}
