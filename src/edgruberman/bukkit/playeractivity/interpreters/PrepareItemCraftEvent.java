package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class PrepareItemCraftEvent extends Interpreter {

    public PrepareItemCraftEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.inventory.PrepareItemCraftEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.inventory.PrepareItemCraftEvent sub = (org.bukkit.event.inventory.PrepareItemCraftEvent) event;
        for (final HumanEntity human : sub.getViewers())
            if (human instanceof Player)
                this.record((Player) human, event);
    }

}
