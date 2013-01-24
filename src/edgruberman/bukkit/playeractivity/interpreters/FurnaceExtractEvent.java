package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class FurnaceExtractEvent extends Interpreter {

    public FurnaceExtractEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.inventory.FurnaceExtractEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        if (!(event instanceof org.bukkit.event.inventory.FurnaceExtractEvent)) return; // registers BlockExpEvent

        final org.bukkit.event.inventory.FurnaceExtractEvent sub = (org.bukkit.event.inventory.FurnaceExtractEvent) event;
        this.record(sub.getPlayer(), event);
    }

}
