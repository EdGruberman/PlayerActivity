package edgruberman.bukkit.playeractivity.consumers.listtag.tags;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.IdlePublisher;
import edgruberman.bukkit.playeractivity.PlayerActive;
import edgruberman.bukkit.playeractivity.PlayerIdle;
import edgruberman.bukkit.playeractivity.StatusTracker;
import edgruberman.bukkit.playeractivity.consumers.listtag.ListTag;
import edgruberman.bukkit.playeractivity.consumers.listtag.Tag;

public class Idle extends Tag implements Observer {

    protected final StatusTracker tracker;

    public Idle(final ConfigurationSection config, final ListTag listTag, final Plugin plugin) {
        super(config, listTag, plugin);

        final long idle = TimeUnit.MILLISECONDS.convert(config.getInt("idle"), TimeUnit.SECONDS);
        final List<String> activity = ( config.isList("activity") ? config.getStringList("activity") : config.getRoot().getStringList("activity") );
        this.tracker = new StatusTracker(plugin, idle);
        for (final String className : activity)
            try {
                this.tracker.addInterpreter(className);
            } catch (final Exception e) {
                plugin.getLogger().warning("Unable to create interpreter for Idle Tag activity: " + className + "; " + e);
            }

        this.tracker.register(this, PlayerActive.class);
        this.tracker.register(this, PlayerIdle.class);
    }

    @Override
    protected void onUnload() {
        this.tracker.clear();
    }

    @Override
    public void update(final Observable o, final Object arg) {
        if (o instanceof IdlePublisher) {
            final PlayerIdle idle = (PlayerIdle) arg;
            this.attach(idle.player, idle.last);
            return;
        }

        final PlayerActive activity = (PlayerActive) arg;
        if (activity.last == null || (activity.occurred - activity.last) < this.tracker.getIdleThreshold()) return;
        this.detach(activity.player);
        return;
    }

}
