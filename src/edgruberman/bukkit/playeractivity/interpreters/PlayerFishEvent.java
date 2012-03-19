package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerFishEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.player.PlayerFishEvent event) {
        this.player = event.getPlayer();
    }

}
