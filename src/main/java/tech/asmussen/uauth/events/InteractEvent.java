package tech.asmussen.uauth.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import tech.asmussen.uauth.UltraAuthenticator;

public class InteractEvent implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (!UltraAuthenticator.currentlyVerifying.contains(event.getPlayer().getUniqueId())) return;
		
		event.setCancelled(true);
	}
}
