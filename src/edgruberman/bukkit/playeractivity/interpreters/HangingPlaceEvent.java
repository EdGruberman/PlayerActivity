package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class HangingPlaceEvent extends Interpreter {

    public HangingPlaceEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.hanging.HangingPlaceEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.hanging.HangingPlaceEvent sub = (org.bukkit.event.hanging.HangingPlaceEvent) event;
        this.record(sub.getPlayer(), event);
    }

}
