package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerMoveBlockEvent extends Interpreter {

    public PlayerMoveBlockEvent(final StatusTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(final edgruberman.bukkit.playeractivity.PlayerMoveBlockEvent event) {
        this.record(event.getPlayer(), event);
    }

}
