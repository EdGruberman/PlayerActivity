package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerEvent extends Interpreter {

    protected PlayerEvent(final StatusTracker tracker, final Class<? extends org.bukkit.event.player.PlayerEvent> event) {
        super(tracker, event);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.player.PlayerEvent sub = (org.bukkit.event.player.PlayerEvent) event;
        this.record(sub.getPlayer(), event);
    }

}
