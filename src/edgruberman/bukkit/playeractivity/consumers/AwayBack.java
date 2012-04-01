package edgruberman.bukkit.playeractivity.consumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.EventTracker;
import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.PlayerActivity;

public class AwayBack implements Observer {

    public boolean overrideIdle = true;
    public String awayFormat = null;
    public String backFormat = null;
    public String defaultReason = null;

    public final EventTracker back;

    private final Plugin plugin;
    private final Map<Player, AwayState> away = new HashMap<Player, AwayState>();
    private boolean enabled = false;

    public AwayBack(final Plugin plugin) {
        this.plugin = plugin;
        this.back = new EventTracker(plugin);
    }

    public boolean start(final List<Class<? extends Interpreter>> interpreters) {
        if (this.backFormat == null) return false;

        this.setEnabled(true);

        final List<Interpreter> instances = new ArrayList<Interpreter>();
        for (final Class<? extends Interpreter> iClass : interpreters)
            try {
                instances.add(iClass.newInstance());
            } catch (final Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "Unable to create activity interpreter: " + iClass.getName(), e);
            }
        this.back.addInterpreters(instances);

        this.back.activityPublisher.addObserver(this);

        return true;
    }

    public void stop() {
        this.setEnabled(false);
        this.back.clear();
        this.away.clear();
    }

    public boolean setAway(final Player player, final String reason) {
        if (Main.listTag != null) Main.listTag.setAway(player);
        final AwayState state = new AwayState(player, System.currentTimeMillis(), reason);
        return this.away.put(player, state) == null;
    }

    public boolean setBack(final Player player) {
        if (Main.listTag != null) Main.listTag.unsetAway(player);
        return this.away.remove(player) != null;
    }

    public boolean isAway(final Player player) {
        return this.away.containsKey(player);
    }

    public AwayState getAwayState(final Player player) {
        return this.away.get(player);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final PlayerActivity activity = (PlayerActivity) arg;
        if (!this.isAway(activity.player)) return;

        activity.player.performCommand("back");
    }

}
