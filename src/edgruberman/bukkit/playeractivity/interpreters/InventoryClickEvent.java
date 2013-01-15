package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class InventoryClickEvent extends Interpreter {

    public InventoryClickEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.inventory.InventoryClickEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.inventory.InventoryClickEvent sub = (org.bukkit.event.inventory.InventoryClickEvent) event;
        if (!(sub.getWhoClicked() instanceof Player)) return;

        this.record((Player) sub.getWhoClicked(), event);
    }

}
