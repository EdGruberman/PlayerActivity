package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class EntityEvent extends Interpreter {

    public EntityEvent(final StatusTracker tracker, final Class<? extends org.bukkit.event.entity.EntityEvent> event) {
        super(tracker, event);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.entity.EntityEvent sub = (org.bukkit.event.entity.EntityEvent) event;
        if (!(sub.getEntity() instanceof Player)) return;

        this.record((Player) sub.getEntity(), event);
    }

}
