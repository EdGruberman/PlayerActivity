package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class BlockDamageEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.block.BlockDamageEvent event) {
        this.player = event.getPlayer();
    }

}
