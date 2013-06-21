package edgruberman.bukkit.playeractivity.consumers.away;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.PlayerActive;
import edgruberman.bukkit.playeractivity.StatusTracker;
import edgruberman.bukkit.playeractivity.messaging.Courier.ConfigurationCourier;

public class AwayBack implements Observer, Listener {

    public final Plugin plugin;
    public final StatusTracker back;
    public final Mentions mentions;

    private final ConfigurationCourier courier;
    private final Map<String, AwayState> away = new HashMap<String, AwayState>();

    public AwayBack(final Plugin plugin, final List<String> activity, final boolean mentions, final ConfigurationCourier courier) {
        this.plugin = plugin;
        this.courier = courier;

        this.back = new StatusTracker(plugin);
        for (final String className : activity)
            try {
                this.back.addInterpreter(className);
            } catch (final Exception e) {
                plugin.getLogger().warning("Unable to create interpreter for AwayBack activity: " + className + "; " + e);
            }

        this.back.register(this, PlayerActive.class);

        this.mentions = (mentions ? new Mentions(plugin, this.courier, this) : null);

        for (final Player player : Bukkit.getOnlinePlayers())
            player.setMetadata("away", new FixedMetadataValue(this.plugin, false));

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent quit) {
        this.away.remove(quit.getPlayer().getName());
    }

    public void unload() {
        if (this.mentions != null) this.mentions.unload();
        this.back.clear();
        this.away.clear();

        for (final Player player : Bukkit.getOnlinePlayers())
            player.removeMetadata("away", this.plugin);
    }

    public boolean setAway(final Player player, final String reason) {
        final AwayState state = new AwayState(player.getName(), System.currentTimeMillis(), reason);
        this.away.put(player.getName(), state);
        player.setMetadata("away", new FixedMetadataValue(this.plugin, true));

        final PlayerAway custom = new PlayerAway(state.player(), state.since, state.reason);
        Bukkit.getServer().getPluginManager().callEvent(custom);
        return true;
    }

    public boolean setBack(final Player player) {
        final AwayState state = this.away.get(player.getName());
        if (state == null) return false;

        this.away.remove(player.getName());
        player.setMetadata("away", new FixedMetadataValue(this.plugin, false));

        final PlayerBack custom = new PlayerBack(state.player(), state.since, state.reason);
        Bukkit.getServer().getPluginManager().callEvent(custom);
        return true;
    }

    public boolean isAway(final Player player) {
        return this.isAway(player.getName());
    }

    public boolean isAway(final String name) {
        return this.away.containsKey(name);
    }

    public AwayState getAwayState(final String name) {
        return this.away.get(name);
    }

    public Set<String> getAway() {
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

        public final String name;
        public final long since;
        public final String reason;

        AwayState(final String name, final long since, final String reason) {
            this.name = name;
            this.since = since;
            this.reason = reason;
        }

        public Player player() {
            return Bukkit.getPlayerExact(this.name);
        }

    }

}
