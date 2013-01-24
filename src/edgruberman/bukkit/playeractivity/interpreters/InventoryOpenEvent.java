package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class InventoryOpenEvent extends Interpreter {

    public InventoryOpenEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.inventory.InventoryOpenEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.inventory.InventoryOpenEvent sub = (org.bukkit.event.inventory.InventoryOpenEvent) event;
        if (!(sub.getPlayer() instanceof Player)) return;

        this.record((Player) sub.getPlayer(), event);
    }

}
