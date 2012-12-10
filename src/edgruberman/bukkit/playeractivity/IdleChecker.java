package edgruberman.bukkit.playeractivity;


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

        // Clean up the reference to this timer so a new one can be created if necessary
        this.publisher.timers.remove(this.player);

        final Long last = this.publisher.activityPublisher.last.get(this.player);
        if (last == null) return; // Player has left server, stop this check

        if (last != this.lastActivity) {
            // Player has recorded newer activity, reschedule a new check
            this.publisher.scheduleIdleCheck(this.player, last);
            return;
        }

        // Player has no new recorded activity, consider the player idle
        this.publisher.publish(this.player, this.lastActivity, occurred, occurred - this.lastActivity);
    }

}
