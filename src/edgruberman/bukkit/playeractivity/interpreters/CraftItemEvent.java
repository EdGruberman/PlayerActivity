package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class CraftItemEvent extends Interpreter {

    public CraftItemEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.inventory.CraftItemEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        if (!(event instanceof org.bukkit.event.inventory.CraftItemEvent)) return;

        final org.bukkit.event.inventory.CraftItemEvent sub = (org.bukkit.event.inventory.CraftItemEvent) event;
        if (!(sub.getWhoClicked() instanceof Player)) return;

        this.record((Player) sub.getWhoClicked(), event);
    }

}
