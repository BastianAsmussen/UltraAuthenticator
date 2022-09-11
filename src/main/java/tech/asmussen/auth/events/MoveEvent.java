package tech.asmussen.auth.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import tech.asmussen.auth.core.UltraAuthenticator;

import java.util.Objects;

public class MoveEvent implements Listener {
	
	@EventHandler
	public void onMovement(PlayerMoveEvent event) {
		
		if (UltraAuthenticator.CURRENTLY_AUTHENTICATING.get(event.getPlayer().getUniqueId()) == null) return;
		
		if (event.getFrom().getBlockX() == Objects.requireNonNull(event.getTo(), "Movement is null!").getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return; // If the player hasn't moved, don't do anything.
		
		event.setCancelled(true);
	}
}
