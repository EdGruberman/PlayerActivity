package edgruberman.bukkit.playeractivity.consumers;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageDisplay;
import edgruberman.bukkit.playeractivity.ActivityPublisher;
import edgruberman.bukkit.playeractivity.EventTracker;
import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.PlayerActivity;
import edgruberman.bukkit.playeractivity.PlayerIdle;

public class ListTag implements Observer, Listener {

    public long idle = -1;
    public String awayTag = null;
    public String idleTag = null;
    public String bedTag = null;

    public final EventTracker tracker;

    private final Plugin plugin;
    private final List<Player> playersInBed = new ArrayList<Player>();

    private final String ignore;

    public ListTag(final Plugin plugin) {
        this.plugin = plugin;
        this.tracker = new EventTracker(plugin);
        this.ignore = plugin.getDescription().getName().toLowerCase() + ".idle.ignore.listtag";
    }

    public boolean start(final List<Class<? extends Interpreter>> interpreters) {
        if ((this.idle <= 0) || interpreters.size() == 0) return false;

        this.tracker.idlePublisher.setThreshold(this.idle);
        this.tracker.idlePublisher.addObserver(this);
        this.tracker.activityPublisher.addObserver(this);
        final List<Interpreter> instances = new ArrayList<Interpreter>();
        for (final Class<? extends Interpreter> iClass : interpreters)
            try {
                instances.add(iClass.newInstance());
            } catch (final Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "Unable to create activity interpreter: " + iClass.getName(), e);
            }
        this.tracker.addInterpreters(instances);

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        return true;
    }

    public void stop() {
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

            this.unsetIdle(activity.player);
            return;
        }

        // Idle
        final PlayerIdle idle = (PlayerIdle) arg;
        if (idle.player.hasPermission(this.ignore)) return;

        this.setIdle(idle.player);
        return;
    }

    private void setTag(final Player player, final String tag) {
        final String name = player.getName().substring(0, Math.min(player.getName().length(), 16 - tag.length()));
        player.setPlayerListName(MessageDisplay.translate(name + tag));
    }

    public void setAway(final Player player) {
        this.setTag(player, this.awayTag);
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
        if (Main.awayBack != null && Main.awayBack.overrideIdle && Main.awayBack.isAway(player))
            return;

        this.setTag(player, this.idleTag);
    }

    public void unsetIdle(final Player player) {
        if (Main.awayBack != null && Main.awayBack.isAway(player)) return;

        if (this.playersInBed.contains(player)) {
            this.setBed(player);
            return;
        }

        this.resetListName(player);
    }

    public void setBed(final Player player) {
        this.setTag(player, this.bedTag);
    }

    public void unsetBed(final Player player) {
        if (Main.awayBack != null && Main.awayBack.isAway(player)) {
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
    public void onPlayerBedEnter(final PlayerBedEnterEvent event) {
        this.setBed(event.getPlayer());
    }

    @EventHandler
    public void onPlayerBedLeave(final PlayerBedLeaveEvent event) {
        this.unsetBed(event.getPlayer());
    }

}
