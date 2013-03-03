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
import org.bukkit.plugin.Plugin;

/** monitors for PlayerActive from the ActivityPublisher and generates a PlayerIdle event when not active for a period */
public final class IdlePublisher extends Observable implements Observer {

    // shared Timer is sufficient as processing an IdleChecker depends on main thread access
    final Timer timer = new Timer();

    final Plugin plugin;
    final long threshold;
    final ActivityPublisher activityPublisher;
    final List<String> idle = new ArrayList<String>();
    final Map<String, TimerTask> tasks = new HashMap<String, TimerTask>();

    IdlePublisher(final Plugin plugin, final ActivityPublisher activityPublisher, final long threshold) {
        this.plugin = plugin;
        this.threshold = threshold;
        this.activityPublisher = activityPublisher;
        this.activityPublisher.addObserver(this);
    }

    /** process PlayerActivity from ActivityPublisher */
    @Override
    public void update(final Observable o, final Object arg) {
        if (this.threshold <= 0) return;

        final PlayerActive active = (PlayerActive) arg;
        this.idle.remove(active.player.getName());
        this.scheduleIdleCheck(active.player.getName(), active.occurred);
    }

    void scheduleIdleCheck(final String player, final long lastActivity) {
        // wait for a pending timer
        if (this.tasks.containsKey(player)) return;

        final IdleChecker idleChecker = new IdleChecker(this, player, lastActivity);

        final long delay = this.threshold - (System.currentTimeMillis() - lastActivity);
        if (delay <= 0) {
            idleChecker.run();
            return;
        }

        // use an external Timer to help adhere to threshold and minimize effects of tick lag
        final TimerTask task = new SynchronousTimerTask(idleChecker);
        this.tasks.put(player, task);
        this.timer.schedule(task, delay);
    }

    void publish(final String player, final long last, final long occurred, final long duration) {
        this.idle.add(player);
        if (this.countObservers() == 0) return;

        this.setChanged();
        this.notifyObservers(new PlayerIdle(Bukkit.getPlayerExact(player), last, occurred, duration));
    }

    void clear() {
        this.activityPublisher.deleteObserver(this);
        this.deleteObservers();
        this.tasks.clear();
        this.timer.cancel();
    }



    /** return task into the main server thread to avoid thread safety issues */
    private class SynchronousTimerTask extends TimerTask {

        private final Runnable runnable;

        private SynchronousTimerTask(final Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            Bukkit.getScheduler().scheduleSyncDelayedTask(IdlePublisher.this.plugin, this.runnable);
        }

    }

}
