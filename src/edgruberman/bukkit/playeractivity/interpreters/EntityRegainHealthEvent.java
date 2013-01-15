package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class EntityRegainHealthEvent extends EntityEvent {

    public EntityRegainHealthEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.entity.EntityRegainHealthEvent.class);
    }

}
