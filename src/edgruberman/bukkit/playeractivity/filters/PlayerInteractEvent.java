package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerInteractEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.player.PlayerInteractEvent event) {
        // TODO - use event.isCancelled() when bug is fixed that doesn't check right clicking on air with item returning true
        if (event.useInteractedBlock() == Result.DENY && event.useItemInHand() == Result.DENY) return;

        this.player = event.getPlayer();
    }

}
