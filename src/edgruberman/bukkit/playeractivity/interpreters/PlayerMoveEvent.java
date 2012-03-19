package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerMoveEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.player.PlayerMoveEvent event) {
        this.player = event.getPlayer();
    }

}
