package edgruberman.bukkit.playeractivity.consumers;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.StatusTracker;
import edgruberman.bukkit.playeractivity.Messenger;
import edgruberman.bukkit.playeractivity.PlayerActive;
import edgruberman.bukkit.playeractivity.interpreters.Interpreter;

public class AwayBack implements Observer, Listener {

    public final boolean overrideIdle;
    public final StatusTracker back;
    public final Mentions mentions;
    public IdleNotify idleNotify = null;

    private final Messenger messenger;
    private final Map<Player, AwayState> away = new HashMap<Player, AwayState>();

    public AwayBack(final Plugin plugin, final ConfigurationSection config, final Messenger messenger) {
        this.messenger = messenger;
        this.overrideIdle = config.getBoolean("overrideIdle");

        this.back = new StatusTracker(plugin);
        for (final String className : config.getStringList("activity"))
            try {
                this.back.addInterpreter(Interpreter.create(className));
            } catch (final Exception e) {
                plugin.getLogger().warning("Unable to create interpreter for AwayBack activity: " + className + "; " + e.getClass().getName() + "; " + e.getMessage());
            }

        this.back.register(this, PlayerActive.class);

        this.mentions = (config.getBoolean("mentions") ? new Mentions(plugin, this.messenger, this) : null);
    }

    public void unload() {
        if (this.mentions != null) this.mentions.unload();
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
        final AwayState state = this.away.get(player);
        if (state == null) return false;

        // Force IdleNotify to process activity before away status is removed
        if (this.overrideIdle && this.idleNotify != null) this.idleNotify.tracker.record(player, PlayerBack.class);

        this.away.remove(player);

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

    @Override
    public void update(final Observable o, final Object arg) {
        final PlayerActive activity = (PlayerActive) arg;
        if (!this.isAway(activity.player)) return;

        activity.player.performCommand("back");
    }



    /** current status of an away player */
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
