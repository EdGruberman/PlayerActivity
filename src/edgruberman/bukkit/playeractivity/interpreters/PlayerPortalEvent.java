package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerPortalEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.player.PlayerPortalEvent event) {
        this.player = event.getPlayer();
    }

}
