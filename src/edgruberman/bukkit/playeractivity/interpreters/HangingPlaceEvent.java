package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class HangingPlaceEvent extends Interpreter {

    public HangingPlaceEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.hanging.HangingPlaceEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.hanging.HangingPlaceEvent sub = (org.bukkit.event.hanging.HangingPlaceEvent) event;
        this.record(sub.getPlayer(), event);
    }

}
