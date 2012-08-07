package edgruberman.bukkit.playeractivity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.timeservice.TimeService;

/** standardized messaging manager for plugins */
public class Messenger implements Listener {

    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    public static Messenger load(final Plugin plugin) {
        return Messenger.load(plugin, null);
    }

    public static Messenger load(final Plugin plugin, final String formats) {
        if (Bukkit.getPluginManager().getPlugin("TimeService") != null) {
            plugin.getLogger().config("Message timestamps will use TimeService plugin for personalized player time zones");
            return new TimeServiceMessenger(plugin, formats);
        }

        final Messenger messenger = new Messenger(plugin, formats);
        plugin.getLogger().config("Message timestamps will use time zone: " + messenger.getTimeZone(null).getDisplayName());
        return messenger;
    }

    protected final Plugin plugin;
    protected final String formats;

    /**
     * create with custom message format base path
     *
     * @param plugin plugin containing message format strings in configuration
     * @param formats configuration path to message format strings
     */
    protected Messenger(final Plugin plugin, final String formats) {
        this.plugin = plugin;
        this.formats = formats;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * create with configuration root as message format base path
     *
     * @param plugin plugin containing message format strings in configuration
     */
    protected Messenger(final Plugin plugin) {
        this(plugin, null);
    }

    public Calendar getNow(final String player) {
        return new GregorianCalendar(this.getTimeZone(player));
    }

    public TimeZone getTimeZone(final String player) {
        return Messenger.DEFAULT_TIME_ZONE;
    }

    /** configuration root containing message format strings */
    public ConfigurationSection getFormats() {
        if (this.formats == null) return this.plugin.getConfig().getRoot();

        return this.plugin.getConfig().getConfigurationSection(this.formats);
    }

    /** message format string supplied in configuration */
    public String getFormat(final String path) {
        return this.getFormats().getString(path);
    }

    /** message format string list supplied in configuration */
    public List<String> getFormatList(final String path) {
        if (this.getFormats().isList(path))
            return this.getFormats().getStringList(path);

        return Arrays.asList(this.getFormat(path));
    }

    /**
     * format a message format string with a standard timestamp option
     *
     * @param format message format string
     * @param timestamp message timestamp
     * @param args arguments to supply to format
     * @return formatted message
     */
    public String format(final String format, final Calendar timestamp, final Object... args) {
        // Prepend time argument
        Object[] argsAll = null;
        argsAll = new Object[args.length + 1];
        argsAll[0] = timestamp;
        if (args.length >= 1) System.arraycopy(args, 0, argsAll, 1, args.length);

        // Format message
        return String.format(format, argsAll);
    }

    /**
     * send message to individual player
     * (managed: format pulled from configuration and creates log entry)
     *
     * @param target where to send
     * @param path configuration path to message format string
     * @param args arguments to supply to format
     */
    public void tell(final CommandSender target, final String path, final Object... args) {
        final Level level = (target instanceof ConsoleCommandSender? Level.FINEST : Level.FINER);
        for (final String format : this.getFormatList(path)) {
            if (format == null) continue;

            final String message = this.tellMessage(target, format, args);
            this.plugin.getLogger().log(level, "[TELL@" + target.getName() + "] " + message);
        }
    }

    /**
     * send message to individual player
     * (unmanaged: format directly provided and no log entry)
     *
     * @param target where to send
     * @param format message format string
     * @param args arguments to supply to format
     * @return formatted message
     */
    public String tellMessage(final CommandSender target, final String format, final Object... args) {
        if (format == null) return null;

        final String message = this.format(format, this.getNow(target.getName()), args);
        target.sendMessage(message);
        return message;
    }

    /**
     * send message to all players with a specific permission
     * (managed: format pulled from configuration and creates log entry)
     *
     * @param permission name of permission player must have in order to receive message
     * @param path configuration path to message format string
     * @param args arguments to supply to format
     */
    public void publish(final String permission, final String path, final Object... args) {
        final Calendar now = new GregorianCalendar(Messenger.DEFAULT_TIME_ZONE);
        for (final String format : this.getFormatList(path)) {
            if (format == null) continue;

            final int count = this.publishMessage(permission, format, args);
            final String message = this.format(format, now, args);
            this.plugin.getLogger().finer("[PUBLISH@" + permission + "(" + count + ")] " + message);
        }
    }

    /**
     * send message to all players with a specific permission
     * (unmanaged: format directly provided and no log entry)
     *
     * @param permission name of permission player must have in order to receive message
     * @param format message format string
     * @param args arguments to supply to format
     * @return count of messages sent
     */
    public int publishMessage(final String permission, final String format, final Object... args) {
        if (format == null) return -1;

        final Calendar now = new GregorianCalendar();
        int count = 0;
        for (final Permissible permissible : Bukkit.getPluginManager().getPermissionSubscriptions(permission))
            if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
                final CommandSender target = (CommandSender) permissible;
                now.setTimeZone(this.getTimeZone(target.getName()));
                target.sendMessage(this.format(format, now, args));
                count++;
            }

        return count;
    }

    /**
     * send message to all players on the server
     * (managed: format pulled from configuration and creates log entry)
     *
     * @param path configuration path to message format string
     * @param args arguments to supply to format
     */
    public void broadcast(final String path, final Object... args) {
        final Calendar now = new GregorianCalendar(Messenger.DEFAULT_TIME_ZONE);
        for (final String format : this.getFormatList(path)) {
            if (format == null) continue;

            final int count = this.broadcastMessage(format, args);
            final String message = this.format(format, now, args);
            this.plugin.getLogger().finest("[BROADCAST(" + count + ")] " + message);
        }
    }

    /**
     * send message to all players on the server
     * (unmanaged: format directly provided and no log entry)
     *
     * @param format message format string
     * @param args arguments to supply to format
     * @return count of messages sent
     */
    public int broadcastMessage(final String format, final Object... args) {
        return this.publishMessage(Server.BROADCAST_CHANNEL_USERS, format, args);
    }

    /**
     * send message to all players in a specific world
     * (managed: format pulled from configuration and creates log entry)
     *
     * @param world world to target players in
     * @param path configuration path to message format string
     * @param args arguments to supply to format
     */
    public void world(final World world, final String path, final Object... args) {
        final Calendar now = new GregorianCalendar(Messenger.DEFAULT_TIME_ZONE);
        for (final String format : this.getFormatList(path)) {
            if (format == null) continue;

            final int count = this.worldMessage(world, format, args);
            final String message = this.format(format, now, args);
            this.plugin.getLogger().finer("[WORLD%" + world.getName() + "(" + count + ")] " + message);
        }
    }

    /**
     * send message to all players in a specific world
     * (unmanaged: format directly provided and no log entry)
     *
     * @param world world to target players in
     * @param format message format string
     * @param args arguments to supply to format
     * @return count of messages sent
     */
    public int worldMessage(final World world, final String format, final Object... args) {
        if (format == null) return -1;

        final Calendar now = new GregorianCalendar();
        int count = 0;
        for (final Player player : world.getPlayers()) {
            now.setTimeZone(this.getTimeZone(player.getName()));
            player.sendMessage(this.format(format, now, args));
            count++;
        }

        return count;
    }



    /** leverage centralized time service plugin */
    public static class TimeServiceMessenger extends Messenger {

        private final TimeService time = (TimeService) Bukkit.getPluginManager().getPlugin("TimeService");

        protected TimeServiceMessenger(final Plugin plugin, final String formats) {
            super(plugin, formats);
        }

        @Override
        public Calendar getNow(final String player) {
            return this.time.getNow(player);
        }

        @Override
        public TimeZone getTimeZone(final String player) {
            return this.time.getTimeZone(player);
        }

    }

}
