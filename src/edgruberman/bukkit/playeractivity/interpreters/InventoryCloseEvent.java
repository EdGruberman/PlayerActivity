package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class InventoryCloseEvent extends Interpreter {

    public InventoryCloseEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.inventory.InventoryCloseEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.inventory.InventoryCloseEvent sub = (org.bukkit.event.inventory.InventoryCloseEvent) event;
        if (!(sub.getPlayer() instanceof Player)) return;

        this.record((Player) sub.getPlayer(), event);
    }

}
