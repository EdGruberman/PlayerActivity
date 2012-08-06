package edgruberman.bukkit.playeractivity.consumers;

import java.util.Observable;
import java.util.Observer;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.StatusTracker;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Messenger;
import edgruberman.bukkit.playeractivity.PlayerIdle;
import edgruberman.bukkit.playeractivity.interpreters.Interpreter;

/** kick players for being idle */
public final class IdleKick implements Observer {

    public final long idle;
    public final StatusTracker tracker;

    private final Messenger messenger;
    private final String ignore;

    public IdleKick(final Plugin plugin, final ConfigurationSection config, final Messenger messenger, final String ignore) {
        this.messenger = messenger;
        this.ignore = ignore;
        this.idle = (long) config.getInt("idle", (int) this.idle / 1000) * 1000;

        this.tracker = new StatusTracker(plugin);
        for (final String className : config.getStringList("activity"))
            try {
                this.tracker.addInterpreter(Interpreter.create(className));
            } catch (final Exception e) {
                plugin.getLogger().warning("Unable to create interpreter for IdleKick activity: " + className + "; " + e.getClass().getName() + "; " + e.getMessage());
            }

        this.tracker.setIdleThreshold(this.idle);
        this.tracker.register(this, PlayerIdle.class);
    }

    public void unload() {
        this.tracker.clear();
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final PlayerIdle idle = (PlayerIdle) arg;
        if (idle.player.hasPermission(this.ignore)) return;

        final String reason = this.messenger.getFormat("+idleKickReason");
        final String message = (reason != null ? String.format(reason, Main.readableDuration(this.idle)) : null);
        idle.player.kickPlayer(message);
    }

}
