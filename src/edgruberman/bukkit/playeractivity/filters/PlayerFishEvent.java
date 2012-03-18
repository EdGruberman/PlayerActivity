package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerFishEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.player.PlayerFishEvent event) {
        this.player = event.getPlayer();
    }

}
