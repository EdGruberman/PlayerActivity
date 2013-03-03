package edgruberman.bukkit.playeractivity;

import java.util.TimerTask;

class IdleChecker implements Runnable {

    private final IdlePublisher publisher;
    private final String player;
    private final long lastActivity;

    IdleChecker(final IdlePublisher publisher, final String player, final long lastActivity) {
        this.publisher = publisher;
        this.player = player;
        this.lastActivity = lastActivity;
    }

    @Override
    public void run() {
        final long occurred = System.currentTimeMillis();

        // clean up the reference to this timer so a new one can be created if necessary
        final TimerTask task = this.publisher.tasks.remove(this.player);
        if (task != null) task.cancel();

        final Long last = this.publisher.activityPublisher.last.get(this.player);
        if (last == null) return; // player has left server, stop this check

        if (last != this.lastActivity) {
            // player has recorded newer activity, reschedule a new check
            this.publisher.scheduleIdleCheck(this.player, last);
            return;
        }

        // player has no new recorded activity, consider the player idle
        this.publisher.publish(this.player, this.lastActivity, occurred, occurred - this.lastActivity);
    }

}
