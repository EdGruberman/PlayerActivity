package edgruberman.bukkit.playeractivity.interpreters;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class EntityDamageEvent extends EntityEvent {

    public EntityDamageEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.entity.EntityDamageEvent.class);
    }

}
