package tech.asmussen.uauth.events;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import tech.asmussen.uauth.UltraAuthenticator;

public class Events implements Listener {
    
    UltraAuthenticator uAuth = new UltraAuthenticator();
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        FileConfiguration authConfig = uAuth.getAuthConfig();

        Player player = event.getPlayer();
        
        if (!authConfig.contains("codes." + player.getUniqueId())) {

            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            GoogleAuthenticatorKey gKey = gAuth.createCredentials();
            
            player.sendMessage("Your authentication code is: " + gKey.getKey());
            player.sendMessage("Please enter the code from your authenticator app and write it in the chat.");
            
            uAuth.authLocked.add(player.getUniqueId());
            
        } else {

            uAuth.authLocked.add(player.getUniqueId());
            
            player.sendMessage("You need to verify your identity before you can do anything else.");
        }
    }
    
    @EventHandler
    public void onMovement(PlayerMoveEvent event) {
        
        Player player = event.getPlayer();
        
        if (uAuth.authLocked.contains(player.getUniqueId())) {
            
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        
        Player player = event.getPlayer();
        
        if (!uAuth.authLocked.contains(player.getUniqueId())) return;
        
        event.setCancelled(true);
        
        try {
    
            int code = Integer.parseInt(event.getMessage());
            
            if (inputCode(player, code)) {
                
                uAuth.authLocked.remove(player.getUniqueId());
                player.sendMessage("You are now verified!");
            }
            
        } catch (NumberFormatException e) {
            
            player.sendMessage("Please enter a valid code.");
        }
    }
    
    private boolean inputCode(Player player, int code) {

        String secretKey = uAuth.getAuthConfig().getString("codes." + player.getUniqueId());

        GoogleAuthenticator gAuth = new GoogleAuthenticator();

        assert secretKey != null;
        
        boolean codeIsValid = gAuth.authorize(secretKey, code);
        
        if (codeIsValid) {

            uAuth.authLocked.remove(player.getUniqueId());

            return true;
        }
        
        return false;
    }
}
