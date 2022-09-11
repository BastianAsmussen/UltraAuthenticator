package tech.asmussen.auth.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import tech.asmussen.auth.util.Utility;

public class DamageEvent extends Utility implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		
		if (!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
		
		if (!userConfig.contains("users." + player.getUniqueId())) return;
		
		playerSend(player, messageConfig.getString("authentication.message"));
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		
		if (event.getDamager() instanceof Player) {
			
			Player player = (Player) event.getDamager();
			
			if (!userConfig.contains("users." + player.getUniqueId())) return;
			
			playerSend(player, messageConfig.getString("authentication.message"));
			
			event.setCancelled(true);
		}
		
		if (event.getEntity() instanceof Player) {
			
			Player player = (Player) event.getEntity();
			
			if (!userConfig.contains("users." + player.getUniqueId())) return;
			
			playerSend(player, messageConfig.getString("authentication.message"));
			
			event.setCancelled(true);
		}
	}
}
