package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class ProjectileHitEvent extends Interpreter {

    public ProjectileHitEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.entity.ProjectileHitEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.entity.ProjectileHitEvent sub = (org.bukkit.event.entity.ProjectileHitEvent) event;
        if (!(sub.getEntity().getShooter() instanceof Player)) return;

        this.record((Player) sub.getEntity().getShooter(), event);
    }

}
