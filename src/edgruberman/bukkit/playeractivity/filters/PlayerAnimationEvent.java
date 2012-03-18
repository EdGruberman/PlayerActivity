package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerAnimationEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.player.PlayerAnimationEvent event) {
        this.player = event.getPlayer();
    }

}
