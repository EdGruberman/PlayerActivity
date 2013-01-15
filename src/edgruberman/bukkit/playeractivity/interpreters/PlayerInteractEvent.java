package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerInteractEvent extends PlayerEvent {

    public PlayerInteractEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerInteractEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        // TODO - use event.isCancelled() when bug is fixed that doesn't check right clicking on air with item returning true
        final org.bukkit.event.player.PlayerInteractEvent playerEvent = (org.bukkit.event.player.PlayerInteractEvent) event;
        if (playerEvent.useInteractedBlock() == Result.DENY && playerEvent.useItemInHand() == Result.DENY) return;

        super.execute(listener, event);
    }

}
