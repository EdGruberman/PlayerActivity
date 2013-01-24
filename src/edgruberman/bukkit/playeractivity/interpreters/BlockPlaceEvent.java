package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class BlockPlaceEvent extends Interpreter {

    public BlockPlaceEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.block.BlockPlaceEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.block.BlockPlaceEvent sub = (org.bukkit.event.block.BlockPlaceEvent) event;
        this.record(sub.getPlayer(), event);
    }

}
