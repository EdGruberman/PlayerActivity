package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class HangingBreakByEntityEvent extends Interpreter {

    public HangingBreakByEntityEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.hanging.HangingBreakByEntityEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.hanging.HangingBreakByEntityEvent sub = (org.bukkit.event.hanging.HangingBreakByEntityEvent) event;
        if (!(sub.getRemover() instanceof Player)) return;

        this.record((Player) sub.getRemover(), event);
    }

}
