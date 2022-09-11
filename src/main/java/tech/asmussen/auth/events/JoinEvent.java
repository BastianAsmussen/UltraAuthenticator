package tech.asmussen.auth.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import tech.asmussen.auth.core.UltraAuthenticator;
import tech.asmussen.auth.util.Utility;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class JoinEvent extends Utility implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		if (!userConfig.contains("users." + player.getUniqueId())) return;
		
		UltraAuthenticator.CURRENTLY_AUTHENTICATING.put(player.getUniqueId(), serverConfig.getInt("authentication.number-of-attempts"));
		
		if (!userConfig.getBoolean("users." + player.getUniqueId() + ".verified")) {
			
			String[] firstTimeMessages = new String[0];
			
			try {
				
				firstTimeMessages = Objects.requireNonNull(messageConfig.getString("authentication.first-time"), "Message is null!")
						.replaceAll("%player%", player.getName())
						.replaceAll("%link%", InetAddress.getLocalHost().getHostAddress() + ":" + UltraAuthenticator.webServerInstance.getPort() + "/" + userConfig.getString("users." + player.getUniqueId() + ".secret"))
						.split("\n");
				
			} catch (UnknownHostException e) {
				
				e.printStackTrace();
			}
			
			for (String message : firstTimeMessages)
				
				determineAndSend(player, message);
			
		} else {
			
			playerSend(player, messageConfig.getString("authentication.message"));
		}
	}
}
