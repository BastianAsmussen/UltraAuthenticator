package tech.asmussen.uauth.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tech.asmussen.uauth.UltraAuthenticator;

import java.util.Objects;

public class JoinEvent implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		if (!UltraAuthenticator.getPlugin().getConfig().contains("2fa." + player.getUniqueId())) return;
		
		String tfaSecret = Objects.requireNonNull(UltraAuthenticator.getPlugin().getConfig().getString("messages.user.2fa.secret"), "Secret key is null!").replaceAll("%s", Objects.requireNonNull(UltraAuthenticator.getPlugin().getConfig().getString("2fa." + player.getUniqueId() + ".key"), "Secret key is null!"));
		String tfaMessage = Objects.requireNonNull(UltraAuthenticator.getPlugin().getConfig().getString("messages.user.2fa.message"), "Message is null!").replaceAll("%p", player.getName());
		
		UltraAuthenticator.currentlyVerifying.add(player.getUniqueId());
		
		if (!UltraAuthenticator.getPlugin().getConfig().getBoolean("2fa." + player.getUniqueId() + ".is-verified"))
			UltraAuthenticator.playerSend(player, tfaSecret);
		
		UltraAuthenticator.playerSend(player, tfaMessage);
	}
}
