package edgruberman.bukkit.playeractivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * Monitors for PlayerActivity from the ActivityPublisher and when no activity resets the timer, generates a PlayerIdle event.
 */
public final class IdlePublisher extends Observable implements Observer, Listener {

    final EventTracker tracker;
    final List<Player> idle = new ArrayList<Player>();
    final Map<Player, Timer> timers = new HashMap<Player, Timer>();
    private long threshold = -1;

    IdlePublisher(final EventTracker tracker) {
        this.tracker = tracker;
    }

    void publish(final Player player, final long last, final long duration) {
        this.idle.add(player);
        if (this.countObservers() == 0) return;

        this.setChanged();
        this.notifyObservers(new PlayerIdle(player, last, duration));
    }

    /**
     * Process PlayerActivity event from ActivityPublisher.
     */
    @Override
    public void update(final Observable o, final Object arg) {
        final PlayerActivity activity = (PlayerActivity) arg;
        this.idle.remove(activity.player);
        this.scheduleIdleCheck(activity.player, activity.occurred);
    }

    void scheduleIdleCheck(final Player player, final long lastActivity) {
        // Wait for a pending timer
        if (this.timers.containsKey(player)) return;

        final IdleChecker idleChecker = new IdleChecker(this, player, lastActivity);

        final long delay = this.threshold - (System.currentTimeMillis() - lastActivity);
        if (delay <= 0) {
            idleChecker.run();
            return;
        }

        final Timer timer = new Timer();
        this.timers.put(player, timer);
        final IdlePublisher that = this;
        timer.schedule(
                new TimerTask() {
                        @Override
                        public void run() {
                            // Get back into the tick queue to ensure no timing/race problems
                            that.tracker.getPlugin().getServer().getScheduler()
                                    .scheduleSyncDelayedTask(that.tracker.getPlugin(), idleChecker);
                        }
                }
                , delay
        );
    }

    @Override
    public void addObserver(final Observer o) {
        if (this.threshold <= 0) new IllegalArgumentException("IdlePublisher threshold must be set to a positive value first");

        super.addObserver(o);
        this.tracker.activityPublisher.addObserver(this);
    }

    @Override
    public void deleteObserver(final Observer o) {
        super.deleteObserver(o);
        if (this.countObservers() != 0) return;

        this.tracker.activityPublisher.deleteObserver(this);
    }

    public List<Player> getIdle() {
        return this.idle;
    }

    public long getThreshold() {
        return this.threshold;
    }

    /**
     * @param threshold duration in seconds at which a player is considered idle if no activity
     */
    public void setThreshold(final long threshold) {
        // TODO update any existing threads with new threshold time (update for those exceeding, extend for those under)
        this.threshold = threshold;
    }

    void remove(final Player player) {
        this.idle.remove(player);
        final Timer timer = this.timers.remove(player);
        if (timer != null) timer.cancel();
    }

    void clear() {
        this.deleteObservers();
        for (final Player player : this.timers.keySet()) this.remove(player);
    }

}
