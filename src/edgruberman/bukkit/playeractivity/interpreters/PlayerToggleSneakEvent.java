package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerToggleSneakEvent extends Interpreter {

    public PlayerToggleSneakEvent(final StatusTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.player.PlayerToggleSneakEvent event) {
        this.record(event.getPlayer(), event);
    }

}
