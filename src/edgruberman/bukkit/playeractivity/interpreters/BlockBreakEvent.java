package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class BlockBreakEvent extends Interpreter {

    public BlockBreakEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.block.BlockBreakEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        if (!(event instanceof org.bukkit.event.block.BlockBreakEvent)) return; // registers BlockExpEvent

        final org.bukkit.event.block.BlockBreakEvent sub = (org.bukkit.event.block.BlockBreakEvent) event;
        this.record(sub.getPlayer(), event);
    }

}
