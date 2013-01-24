package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class BlockDamageEvent extends Interpreter {

    public BlockDamageEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.block.BlockDamageEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.block.BlockDamageEvent sub = (org.bukkit.event.block.BlockDamageEvent) event;
        this.record(sub.getPlayer(), event);
    }

}
