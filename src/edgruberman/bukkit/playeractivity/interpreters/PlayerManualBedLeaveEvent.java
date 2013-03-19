package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.StatusTracker;

public class PlayerManualBedLeaveEvent extends PlayerEvent {

    private static final long SLEEP_FAILED_TICKS = 23460; //  bed leave in morning after failed sleep
    private static final long SLEEP_SUCCESS_TICKS = 0; // bed leave in morning after sleep completes

    public PlayerManualBedLeaveEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.player.PlayerBedLeaveEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.player.PlayerBedLeaveEvent leave = ( org.bukkit.event.player.PlayerBedLeaveEvent) event;
        if (leave.getBed().getWorld().getTime() == PlayerManualBedLeaveEvent.SLEEP_FAILED_TICKS) return;
        if (leave.getBed().getWorld().getTime() == PlayerManualBedLeaveEvent.SLEEP_SUCCESS_TICKS) return;
        super.onExecute(event);
    }

}
