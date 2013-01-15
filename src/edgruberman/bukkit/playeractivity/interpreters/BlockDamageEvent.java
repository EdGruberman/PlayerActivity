package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class BlockDamageEvent extends Interpreter {

    public BlockDamageEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.block.BlockDamageEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.block.BlockDamageEvent sub = (org.bukkit.event.block.BlockDamageEvent) event;
        this.record(sub.getPlayer(), event);
    }

}
