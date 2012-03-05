package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import edgruberman.bukkit.playeractivity.Interpreter;

public class EntityDamageEvent extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final org.bukkit.event.entity.EntityDamageEvent event) {
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

        this.tracker.record(player, event);
    }

}
