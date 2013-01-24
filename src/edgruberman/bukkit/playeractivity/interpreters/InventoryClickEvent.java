package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class InventoryClickEvent extends Interpreter {

    public InventoryClickEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.inventory.InventoryClickEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.inventory.InventoryClickEvent sub = (org.bukkit.event.inventory.InventoryClickEvent) event;
        if (!(sub.getWhoClicked() instanceof Player)) return;

        this.record((Player) sub.getWhoClicked(), event);
    }

}
