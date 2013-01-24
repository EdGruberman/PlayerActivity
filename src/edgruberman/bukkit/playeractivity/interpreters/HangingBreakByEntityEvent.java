package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class HangingBreakByEntityEvent extends Interpreter {

    public HangingBreakByEntityEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.hanging.HangingBreakByEntityEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.hanging.HangingBreakByEntityEvent sub = (org.bukkit.event.hanging.HangingBreakByEntityEvent) event;
        if (!(sub.getRemover() instanceof Player)) return;

        this.record((Player) sub.getRemover(), event);
    }

}
