package edgruberman.bukkit.playeractivity.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.permissions.Permissible;

/**
 * collection of one or more {@link org.bukkit.command.CommandSender
 * CommandSender}s
 * @author EdGruberman (ed@rjump.com)
 * @version 4.0.0
 */
public abstract class Recipients {

    public static TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    /** retrieve "TimeZone" MetadataValue for CommandSender */
    public static TimeZone getTimeZone(final CommandSender sender) {
        if (!(sender instanceof Metadatable))
            return Recipients.DEFAULT_TIME_ZONE;

        final Metadatable meta = (Metadatable) sender;
        final List<MetadataValue> values = meta.getMetadata("TimeZone");
        if (values.size() == 0)
            return Recipients.DEFAULT_TIME_ZONE;

        return (TimeZone) values.get(0).value();
    }



    protected Recipients() {}

    /** calculate recipients */
    public abstract List<CommandSender> targets();

    /** generate receipt */
    public abstract Confirmation confirm(final Message sent, final List<CommandSender> received);



    /**
     * single CommandSender
     * @version 4.0.0
     */
    public static class Sender extends Recipients {

        public static Sender create(final CommandSender target) {
            if (target instanceof ConsoleCommandSender) return new ConsoleSender((ConsoleCommandSender) target);
            return new Sender(target);
        }



        protected CommandSender target;

        protected Sender(final CommandSender target) {
            this.target = target;
        }

        @Override
        public List<CommandSender> targets() {
            final List<CommandSender> result = new ArrayList<CommandSender>();
            result.add(this.target);
            return result;
        }

        @Override
        public Confirmation confirm(final Message sent, final List<CommandSender> received) {
            return new Confirmation(this.getLevel(), received, "[SEND>{1}] {0}", sent, this.target.getName());
        }

        protected Level getLevel() {
            return Level.FINER;
        }

    }



    /**
     * the server ConsoleCommandSender
     * @version 4.0.0
     */
    public static class ConsoleSender extends Sender {

        protected ConsoleSender(final ConsoleCommandSender sender) {
           super(sender);
        }

        /** easy filtering of console messages that already appear in log */
        @Override
        protected Level getLevel() {
            return Level.FINEST;
        }

    }



    /**
     * CommandSenders that have the specified permission set true
     * @version 4.0.0
     */
    public static class PermissionSubscribers extends Recipients {

        protected String permission;

        public PermissionSubscribers(final String permission) {
            this.permission = permission;
        }

        public String getPermission() {
            return this.permission;
        }

        @Override
        public List<CommandSender> targets() {
            final List<CommandSender> result = new ArrayList<CommandSender>();
            for (final Permissible permissible : Bukkit.getPluginManager().getPermissionSubscriptions(this.permission)) {
                if (permissible instanceof CommandSender && permissible.hasPermission(this.permission)) {
                    final CommandSender target = (CommandSender) permissible;
                    result.add(target);
                }
            }
            return result;
        }

        @Override
        public Confirmation confirm(final Message sent, final List<CommandSender> received) {
            return new Confirmation(Level.FINER, received, "[PUBLISH-{1}({2})] {0}", sent, this.permission, received.size());
        }

    }



    /**
     * all players in server
     * @version 4.0.0
     */
    public static class ServerPlayers extends PermissionSubscribers {

        public ServerPlayers() {
            super(Server.BROADCAST_CHANNEL_USERS);
        }

        @Override
        public Confirmation confirm(final Message sent, final List<CommandSender> received) {
            return new Confirmation(Level.FINEST, received, "[BROADCAST({1})] {0}", sent, received.size());
        }

    }



    /**
     * players in the specified world
     * @version 4.0.0
     */
    public static class WorldPlayers extends Recipients {

        protected final World world;

        public WorldPlayers(final World world) {
            this.world = world;
        }

        public World getWorld() {
            return this.world;
        }

        @Override
        public List<CommandSender> targets() {
            final List<CommandSender> result = new ArrayList<CommandSender>();
            for (final Player player : this.world.getPlayers()) {
                if (player.hasPermission(Server.BROADCAST_CHANNEL_USERS)) result.add(player);
            }
            return result;
        }

        @Override
        public Confirmation confirm(final Message sent, final List<CommandSender> received) {
            return new Confirmation(Level.FINE, received, "[WORLD%{1}({2})] {0}", sent, this.world.getName(), received.size());
        }

    }

}