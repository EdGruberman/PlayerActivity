package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class EntityDamageByEntityEvent extends Interpreter {

    public EntityDamageByEntityEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.entity.EntityDamageByEntityEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.entity.EntityDamageByEntityEvent sub = (org.bukkit.event.entity.EntityDamageByEntityEvent) event;
        if (!(sub.getDamager() instanceof Player)) return;

        this.record((Player) sub.getDamager(), event);
    }

}
