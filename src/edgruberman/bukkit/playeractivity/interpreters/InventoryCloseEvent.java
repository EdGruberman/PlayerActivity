package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class InventoryCloseEvent extends Interpreter {

    public InventoryCloseEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.inventory.InventoryCloseEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.inventory.InventoryCloseEvent sub = (org.bukkit.event.inventory.InventoryCloseEvent) event;
        if (!(sub.getPlayer() instanceof Player)) return;

        this.record((Player) sub.getPlayer(), event);
    }

}
