package tech.asmussen.auth.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import tech.asmussen.auth.core.UltraAuthenticator;

public class InteractEvent implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (UltraAuthenticator.CURRENTLY_AUTHENTICATING.get(event.getPlayer().getUniqueId()) == null) return;
		
		event.setCancelled(true);
	}
}
