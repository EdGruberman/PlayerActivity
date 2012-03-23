package edgruberman.bukkit.playeractivity.interpreters.AwayBack;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.Main;

public class PlayerBackCommand extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
        if (!Main.awayBack.isEnabled() || !Main.idleKick.awayBroadcastOverride || !Main.awayBack.isAway(event.getPlayer())) return;

        if (!(event.getMessage().equalsIgnoreCase("/back") || event.getMessage().startsWith("/back "))) return;

        this.player = event.getPlayer();
    }

}
