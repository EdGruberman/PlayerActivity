package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerMoveEvent extends Interpreter {

    public PlayerMoveEvent(final StatusTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.player.PlayerMoveEvent event) {
        this.record(event.getPlayer(), event);
    }

}
