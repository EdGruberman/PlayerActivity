package edgruberman.bukkit.playeractivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/** monitors for PlayerActive from the ActivityPublisher and generates a PlayerIdle event when not active for a period */
public final class IdlePublisher extends Observable implements Observer {

    final StatusTracker tracker;
    final List<Player> idle = new ArrayList<Player>();
    final Map<Player, Timer> timers = new HashMap<Player, Timer>();

    long threshold = 60000; // milliseconds

    IdlePublisher(final StatusTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void addObserver(final Observer o) {
        super.addObserver(o);

        // pull last known activity (or use now if none) to start idle timer in case no further activity from player
        for (final Player player : Bukkit.getServer().getOnlinePlayers())
            if (!this.timers.keySet().contains(player)) {
                Long occurred = this.tracker.activityPublisher.last.get(player);
                if (occurred == null) occurred = System.currentTimeMillis();
                this.update(this, new PlayerActive(player, null, occurred, Event.class));
            }


        this.tracker.activityPublisher.addObserver(this);
    }

    @Override
    public void deleteObserver(final Observer o) {
        super.deleteObserver(o);
        if (this.countObservers() != 0) return;

        this.tracker.activityPublisher.deleteObserver(this);
    }

    /** process PlayerActivity from ActivityPublisher */
    @Override
    public void update(final Observable o, final Object arg) {
        final PlayerActive active = (PlayerActive) arg;
        this.idle.remove(active.player);
        this.scheduleIdleCheck(active.player, active.occurred);
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

        // use an external Timer to help adhere to threshold and minimize effects of tick lag
        final Timer timer = new Timer();
        this.timers.put(player, timer);
        timer.schedule(new SynchronousTimerTask(idleChecker), delay);
    }

    void publish(final Player player, final long last, final long occurred, final long duration) {
        this.idle.add(player);
        if (this.countObservers() == 0) return;

        this.setChanged();
        this.notifyObservers(new PlayerIdle(player, last, occurred, duration));
    }

    void clear() {
        this.tracker.activityPublisher.deleteObserver(this);
        this.deleteObservers();
        for (final Timer timer : this.timers.values()) timer.cancel();
        this.timers.clear();
    }



    /** return task into the main server thread to avoid thread safety issues */
    private class SynchronousTimerTask extends TimerTask {

        private final Runnable runnable;

        private SynchronousTimerTask(final Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            Bukkit.getScheduler().scheduleSyncDelayedTask(IdlePublisher.this.tracker.getPlugin(), this.runnable);
        }

    }

}
