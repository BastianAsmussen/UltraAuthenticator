package tech.asmussen.uauth;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {

    UltraAuthenticator uAuth = new UltraAuthenticator();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        FileConfiguration authConfig = uAuth.getAuthConfig();

        Player player = event.getPlayer();

        if (!authConfig.contains("Codes." + player.getUniqueId())) {

            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            GoogleAuthenticatorKey gKey = gAuth.createCredentials();

            /*
             * Create a QR code generator class.
             * Create a website to display QR code with gKey.
             * Send the message with the website URL.
             */

        } else {

            uAuth.authLocked.add(player.getUniqueId());

            /*
             * Send player message informing them about needing to verify their identity.
             */
        }
    }

    private boolean inputCode(Player player, short code) {

        String secretKey = uAuth.getAuthConfig().getString("Codes." + player.getUniqueId());

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
