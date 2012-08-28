package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class ProjectileHitEvent extends Interpreter {

    public ProjectileHitEvent(final StatusTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.entity.ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        this.record((Player) event.getEntity().getShooter(), event);
    }

}
