package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class BlockIgniteEvent extends Interpreter {

    public BlockIgniteEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.block.BlockIgniteEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.block.BlockIgniteEvent sub = (org.bukkit.event.block.BlockIgniteEvent) event;
        this.record(sub.getPlayer(), event);
    }

}
