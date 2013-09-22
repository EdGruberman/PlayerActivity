package edgruberman.bukkit.playeractivity.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.PluginManager;

/**
 * list of targets for message delivery
 * @author EdGruberman (ed@rjump.com)
 * @version 6.0.0
 */
public abstract class RecipientList extends ArrayList<CommandSender> {

    private static final long serialVersionUID = 1L;

    protected RecipientList() {
        super();
    }

    protected RecipientList(final int initialCapacity) {
        super(initialCapacity);
    }

    /** generate receipt */
    public abstract Message.Confirmation confirm(final Message sent, final List<CommandSender> received);





    /**
     * single CommandSender
     * @version 6.0.0
     */
    public static class Sender extends RecipientList {

        private static final long serialVersionUID = 1L;

        public static Sender create(final CommandSender target) {
            if (target instanceof ConsoleCommandSender) return new Console((ConsoleCommandSender) target);
            return new Sender(target);
        }



        protected Sender(final CommandSender target) {
            super(1);
            this.add(target);
        }

        @Override
        public Message.Confirmation confirm(final Message sent, final List<CommandSender> received) {
            return new Message.Confirmation(this.getLevel(), received, "[SEND>{1}] {0}", sent, this.get(0).getName());
        }

        protected Level getLevel() {
            return Level.FINER;
        }

    }





    /**
     * the server ConsoleCommandSender
     * @version 6.0.0
     */
    public static class Console extends Sender {

        private static final long serialVersionUID = 1L;

        public static Console create(final ConsoleCommandSender target) {
            return new Console(target);
        }



        protected Console(final ConsoleCommandSender sender) {
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
     * @version 6.0.0
     */
    public static class Subscribers extends RecipientList {

        private static final long serialVersionUID = 1L;

        public static Subscribers create(final PluginManager pm, final String permission) {
            return new Subscribers(pm, permission);
        }



        protected PluginManager pm;
        protected String permission;

        protected Subscribers(final PluginManager pm, final String permission) {
            this.pm = pm;
            this.permission = permission;

            final Set<Permissible> permissibles = this.pm.getPermissionSubscriptions(this.permission);
            for (final Permissible permissible : permissibles) {
                if (permissible instanceof CommandSender && permissible.hasPermission(this.permission)) {
                    final CommandSender target = (CommandSender) permissible;
                    this.add(target);
                }
            }
        }

        public String getPermission() {
            return this.permission;
        }

        @Override
        public Message.Confirmation confirm(final Message sent, final List<CommandSender> received) {
            return new Message.Confirmation(Level.FINER, received, "[PUBLISH-{1}({2})] {0}", sent, this.permission, received.size());
        }

    }





    /**
     * all players in server
     * @version 6.0.0
     */
    public static class ServerPlayers extends Subscribers {

        private static final long serialVersionUID = 1L;

        public static ServerPlayers create(final Server server) {
            return new ServerPlayers(server);
        }



        protected ServerPlayers(final Server server) {
            super(server.getPluginManager(), Server.BROADCAST_CHANNEL_USERS);
        }

        @Override
        public Message.Confirmation confirm(final Message sent, final List<CommandSender> received) {
            return new Message.Confirmation(Level.FINEST, received, "[BROADCAST({1})] {0}", sent, received.size());
        }

    }





    /**
     * players in the specified world
     * @version 6.0.0
     */
    public static class WorldPlayers extends RecipientList {

        private static final long serialVersionUID = 1L;

        public static WorldPlayers create(final World world) {
            return new WorldPlayers(world);
        }



        protected final World world;

        protected WorldPlayers(final World world) {
            this.world = world;

            for (final Player player : this.world.getPlayers()) {
                if (player.hasPermission(Server.BROADCAST_CHANNEL_USERS)) {
                    this.add(player);
                }
            }
        }

        public World getWorld() {
            return this.world;
        }

        @Override
        public Message.Confirmation confirm(final Message sent, final List<CommandSender> received) {
            return new Message.Confirmation(Level.FINE, received, "[WORLD%{1}({2})] {0}", sent, this.world.getName(), received.size());
        }

    }

}