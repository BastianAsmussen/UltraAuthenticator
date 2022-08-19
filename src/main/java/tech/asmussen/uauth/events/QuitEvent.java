package tech.asmussen.uauth.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tech.asmussen.uauth.UltraAuthenticator;

public class QuitEvent implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		
		UltraAuthenticator.currentlyVerifying.remove(event.getPlayer().getUniqueId());
	}
}
