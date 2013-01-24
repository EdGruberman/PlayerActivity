package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class EntityDamageByEntityEvent extends Interpreter {

    public EntityDamageByEntityEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.entity.EntityDamageByEntityEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.entity.EntityDamageByEntityEvent sub = (org.bukkit.event.entity.EntityDamageByEntityEvent) event;
        if (!(sub.getDamager() instanceof Player)) return;

        this.record((Player) sub.getDamager(), event);
    }

}
