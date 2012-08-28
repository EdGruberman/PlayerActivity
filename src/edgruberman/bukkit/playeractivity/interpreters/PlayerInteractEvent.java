package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerInteractEvent extends Interpreter {

    public PlayerInteractEvent(final StatusTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.player.PlayerInteractEvent event) {
        // TODO - use event.isCancelled() when bug is fixed that doesn't check right clicking on air with item returning true
        if (event.useInteractedBlock() == Result.DENY && event.useItemInHand() == Result.DENY) return;

        this.record(event.getPlayer(), event);
    }

}
