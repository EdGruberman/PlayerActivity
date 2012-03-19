package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerLoginEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.player.PlayerLoginEvent event) {
        this.player = event.getPlayer();
    }

}
