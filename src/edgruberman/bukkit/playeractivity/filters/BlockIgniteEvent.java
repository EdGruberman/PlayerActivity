package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class BlockIgniteEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.block.BlockIgniteEvent event) {
        this.player = event.getPlayer();
    }

}
