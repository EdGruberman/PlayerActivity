package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerItemHeldEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.player.PlayerItemHeldEvent event) {
        this.player = event.getPlayer();
    }

}
