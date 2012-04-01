package edgruberman.bukkit.playeractivity.consumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.EventTracker;
import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.PlayerActivity;

public class AwayBack implements Observer, Listener {

    public boolean overrideIdle = true;
    public String awayFormat = null;
    public String backFormat = null;
    public String defaultReason = null;
    public String mentionsFormat = null;

    public final EventTracker back;

    private final Plugin plugin;
    private final Map<Player, AwayState> away = new HashMap<Player, AwayState>();
    private boolean enabled = false;
    public Mentions mentions = null;

    public AwayBack(final Plugin plugin) {
        this.plugin = plugin;
        this.back = new EventTracker(plugin);
    }

    public boolean start(final List<Class<? extends Interpreter>> interpreters) {
        if (this.backFormat == null) return false;

        this.enabled = true;

        final List<Interpreter> instances = new ArrayList<Interpreter>();
        for (final Class<? extends Interpreter> iClass : interpreters)
            try {
                instances.add(iClass.newInstance());
            } catch (final Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "Unable to create activity interpreter: " + iClass.getName(), e);
            }
        this.back.addInterpreters(instances);

        this.back.activityPublisher.addObserver(this);

        if (this.mentionsFormat != null) {
            this.mentions = new Mentions(this.plugin, this);
            this.mentions.start();
        }

        return true;
    }

    public void stop() {
        if (this.mentions != null) {
            this.mentions.stop();
            this.mentions = null;
        }
        this.enabled = false;
        this.back.clear();
        this.away.clear();
    }

    public boolean setAway(final Player player, final String reason) {
        final AwayState state = new AwayState(player, System.currentTimeMillis(), reason);
        final PlayerAway custom = new PlayerAway(state.player, state.since, state.reason);
        Bukkit.getServer().getPluginManager().callEvent(custom);

        return this.away.put(player, state) == null;
    }

    public boolean setBack(final Player player) {
        final AwayState state = this.away.remove(player);
        if (state == null) return false;

        final PlayerBack custom = new PlayerBack(state.player, state.since, state.reason);
        Bukkit.getServer().getPluginManager().callEvent(custom);

        return true;
    }

    public boolean isAway(final Player player) {
        return this.away.containsKey(player);
    }

    public AwayState getAwayState(final Player player) {
        return this.away.get(player);
    }

    public Set<Player> getAway() {
        return this.away.keySet();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final PlayerActivity activity = (PlayerActivity) arg;
        if (!this.isAway(activity.player)) return;

        activity.player.performCommand("back");
    }

    /**
     * Current status of an away player.
     */
    public class AwayState {

        public final Player player;
        public final long since;
        public final String reason;

        AwayState(final Player player, final long since, final String reason) {
            this.player = player;
            this.since = since;
            this.reason = reason;
        }

    }

}
