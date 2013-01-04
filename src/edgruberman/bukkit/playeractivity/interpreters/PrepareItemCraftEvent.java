package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class PrepareItemCraftEvent extends Interpreter {

    public PrepareItemCraftEvent(final StatusTracker tracker) {
        super(tracker);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEvent(final org.bukkit.event.inventory.PrepareItemCraftEvent event) {
        for (final HumanEntity human : event.getViewers())
            if (human instanceof Player)
                this.record((Player) human, event);
    }

}
