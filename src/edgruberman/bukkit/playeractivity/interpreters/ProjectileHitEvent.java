package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class ProjectileHitEvent extends Interpreter {

    public ProjectileHitEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.entity.ProjectileHitEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.entity.ProjectileHitEvent sub = (org.bukkit.event.entity.ProjectileHitEvent) event;
        if (!(sub.getEntity().getShooter() instanceof Player)) return;

        this.record((Player) sub.getEntity().getShooter(), event);
    }

}
