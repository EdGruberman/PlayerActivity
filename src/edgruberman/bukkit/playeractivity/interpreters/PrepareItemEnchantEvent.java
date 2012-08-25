package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PrepareItemEnchantEvent extends Interpreter {

    public PrepareItemEnchantEvent(final StatusTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.enchantment.PrepareItemEnchantEvent event) {
        this.record(event.getEnchanter(), event);
    }

}
