package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.Tracker;

public final class EntityRegainHealthEventListener extends EventListener {

    public EntityRegainHealthEventListener(final Tracker tracker) {
        super(tracker);
        super.register(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final EntityRegainHealthEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player)) return;

        final Player player = (Player) event.getEntity();
        this.record(player, event);
    }

}
