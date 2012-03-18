package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerBucketFillEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.player.PlayerBucketFillEvent event) {
        this.player = event.getPlayer();
    }

}
