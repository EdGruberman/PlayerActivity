package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.Tracker;

public final class EntityDamageEventListener extends EventListener {

    public EntityDamageEventListener(final Tracker tracker) {
        super(tracker);
        super.register(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final EntityDamageEvent event) {
        if (event.isCancelled()) return;

        Player player = null;
        if (event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();

        } else if (event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
            if (!(edbee.getEntity() instanceof Player)) return;

            player = (Player) event.getEntity();

        } else {
            return;
        }

        this.record(player, event);
    }

}
