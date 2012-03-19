package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class BlockBreakEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.block.BlockBreakEvent event) {
        this.player = event.getPlayer();
    }

}
