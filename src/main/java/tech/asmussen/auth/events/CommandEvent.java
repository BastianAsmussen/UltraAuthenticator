package tech.asmussen.auth.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import tech.asmussen.auth.util.Utility;

public class CommandEvent extends Utility implements Listener {
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		
		Player player = event.getPlayer();
		
		if (!userConfig.contains("users." + player.getUniqueId())) return;
		
		playerSend(player, messageConfig.getString("authentication.message"));
		
		event.setCancelled(true);
	}
}
