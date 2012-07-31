package edgruberman.bukkit.playeractivity.consumers;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.ActivityPublisher;
import edgruberman.bukkit.playeractivity.EventTracker;
import edgruberman.bukkit.playeractivity.Messenger;
import edgruberman.bukkit.playeractivity.PlayerActivity;
import edgruberman.bukkit.playeractivity.PlayerIdle;

public class ListTag implements Observer, Listener {

    public final long idle;
    public final EventTracker tracker;
    public AwayBack awayBack = null;

    private final Messenger messenger;
    private final List<Player> playersInBed = new ArrayList<Player>();
    private final String ignore;

    public ListTag(final Plugin plugin, final ConfigurationSection config, final Messenger messenger, final String ignore) {
        this.messenger = messenger;
        this.ignore = ignore;
        this.idle = (long) config.getInt("idle", (int) this.idle / 1000) * 1000;

        this.tracker = new EventTracker(plugin);
        for (final String className : config.getStringList("activity"))
            try {
                this.tracker.addInterpreter(EventTracker.newInterpreter(className));
            } catch (final Exception e) {
                plugin.getLogger().warning("Unable to create interpreter for ListTag activity: " + className + "; " + e.getClass().getName() + "; " + e.getMessage());
            }

        this.tracker.activityPublisher.addObserver(this);
        this.tracker.idlePublisher.setThreshold(this.idle);
        this.tracker.idlePublisher.addObserver(this);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unload() {
        HandlerList.unregisterAll(this);
        this.tracker.clear();
        this.playersInBed.clear();
    }

    @Override
    public void update(final Observable o, final Object arg) {
        // Back from idle
        if (o instanceof ActivityPublisher) {
            final PlayerActivity activity = (PlayerActivity) arg;
            if (activity.last == null || (activity.occurred - activity.last) < this.idle || activity.player.hasPermission(this.ignore))
                return;

            if (this.isAwayOverriding(activity.player) || activity.player.hasPermission(this.ignore)) return;

            this.unsetIdle(activity.player);
            return;
        }

        // Idle
        final PlayerIdle idle = (PlayerIdle) arg;
        if (this.isAwayOverriding(idle.player) || idle.player.hasPermission(this.ignore)) return;

        this.setIdle(idle.player);
        return;
    }

    private void setTag(final Player player, final String tag) {
        final String name = player.getName().substring(0, Math.min(player.getName().length(), 16 - tag.length()));
        player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', name + tag));
    }

    public void setAway(final Player player) {
        this.setTag(player, this.messenger.getFormat("listTag.+away"));
    }

    public void unsetAway(final Player player) {
        if (this.tracker.idlePublisher.getIdle().contains(player)) {
            this.setIdle(player);
            return;
        }

        if (this.playersInBed.contains(player)) {
            this.setBed(player);
            return;
        }

        this.resetListName(player);
    }

    public void setIdle(final Player player) {
        this.setTag(player, this.messenger.getFormat("listTag.+idle"));
    }

    public void unsetIdle(final Player player) {
        if (this.playersInBed.contains(player)) {
            this.setBed(player);
            return;
        }

        this.resetListName(player);
    }

    private boolean isAwayOverriding(final Player player) {
        if (this.awayBack == null) return false;

        if (!this.awayBack.overrideIdle) return false;

        return this.awayBack.isAway(player);
    }

    public void setBed(final Player player) {
        this.setTag(player, this.messenger.getFormat("listTag.+bed"));
    }

    public void unsetBed(final Player player) {
        if (this.awayBack != null && this.awayBack.isAway(player)) {
            this.setAway(player);
            return;
        }

        if (this.tracker.idlePublisher.getIdle().contains(player)) {
            this.setIdle(player);
            return;
        }

        this.resetListName(player);
    }

    public void disable() {
        for (final Player player : Bukkit.getServer().getOnlinePlayers())
            this.resetListName(player);
    }

    public void resetListName(final Player player) {
        player.setPlayerListName(player.getName());
    }

    @EventHandler
    public void onPlayerAway(final PlayerAway event) {
        this.setAway(event.getPlayer());
    }

    @EventHandler
    public void onPlayerBack(final PlayerBack event) {
        this.unsetAway(event.getPlayer());
    }

    @EventHandler
    public void onPlayerBedEnter(final PlayerBedEnterEvent event) {
        this.setBed(event.getPlayer());
    }

    @EventHandler
    public void onPlayerBedLeave(final PlayerBedLeaveEvent event) {
        this.unsetBed(event.getPlayer());
    }

}
