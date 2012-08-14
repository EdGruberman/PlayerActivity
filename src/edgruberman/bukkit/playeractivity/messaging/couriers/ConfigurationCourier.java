package edgruberman.bukkit.playeractivity.messaging.couriers;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.messaging.Courier;
import edgruberman.bukkit.playeractivity.messaging.Message;
import edgruberman.bukkit.playeractivity.messaging.Recipients;
import edgruberman.bukkit.playeractivity.messaging.messages.ConfigurationMessage;
import edgruberman.bukkit.playeractivity.messaging.recipients.PermissionSubscribers;
import edgruberman.bukkit.playeractivity.messaging.recipients.Sender;
import edgruberman.bukkit.playeractivity.messaging.recipients.ServerPlayers;
import edgruberman.bukkit.playeractivity.messaging.recipients.WorldPlayers;

/**
 * handles message delivery and logging;
 * uses message patterns stored in a {@link org.bukkit.configuration.ConfigurationSection ConfigurationSection}
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 1.0.0
 */
public class ConfigurationCourier extends Courier {

    /** path to section containing message patterns; null if base should be used */
    protected final String basePath;

    /** section containing message patterns; null if basePath should be used */
    protected final ConfigurationSection base;

    /**
     * create a {@link Courier} that uses message patterns stored in a specific section of the owning plugin's config.yml file
     *
     * @param plugin owner
     * @param basePath path to section containing message patterns
     */
    public ConfigurationCourier(final Plugin plugin, final String basePath) {
        super(plugin);
        this.basePath = basePath;
        this.base = null;
    }

    /**
     * create a {@link Courier} that uses message patterns stored anywhere in the owning plugin's config.yml file
     *
     * @param plugin owner
     */
    public ConfigurationCourier(final Plugin plugin) {
        this(plugin, (String) null);
    }

    /**
     * create a {@link Courier} that uses message patterns in the supplied section
     *
     * @param plugin owner
     * @param base section containing message patterns
     */
    public ConfigurationCourier(final Plugin plugin, final ConfigurationSection base) {
        super(plugin);
        this.basePath = null;
        this.base = base;
    }

    /** returns the section containing message patterns */
    public ConfigurationSection getBase() {
        if (this.base != null)
            return this.base;

        if (this.basePath != null)
            return this.plugin.getConfig().getConfigurationSection(this.basePath);

        return this.plugin.getConfig();
    }

    /**
     * retrieve a message pattern from the configuration and format with supplied arguments
     *
     * @param path configuration path to message pattern stored in base configuration
     */
    public String format(final String path, final Object... arguments) {
        return MessageFormat.format(this.getBase().getString(path), arguments);
    }

    /**
     * build a message based on the pattern in the configuration then deliver to recipients and record log entry
     * (if path leads to a string list, a message will be delivered for each string)
     *
     * @param path configuration path to message pattern stored in base
     */
    public void submit(final Recipients recipients, final String path, final Object... arguments) {
        final List<? extends Message> messages = ConfigurationMessage.create(this.getBase(), path, arguments);
        for (final Message message : messages)
            this.submit(recipients, message);

        if (messages.size() == 0)
            this.plugin.getLogger().log(Level.FINEST, "Message definition missing for {0}", path);
    }



    // ---- convenience methods ----

    /**
     * {@inheritDoc}
     * (if path leads to a string list, a message will be delivered for each string)
     *
     * @param path configuration path to message pattern stored in base
     */
    @Override
    public void send(final CommandSender sender, final String path, final Object... arguments) {
        final Recipients recipients = new Sender(sender);
        this.submit(recipients, path, arguments);
    }

    /**
     * {@inheritDoc}
     * (if path leads to a string list, a message will be delivered for each string)
     *
     * @param path configuration path to message pattern stored in base
     */
    @Override
    public void broadcast(final String path, final Object... arguments) {
        final Recipients recipients = new ServerPlayers();
        this.submit(recipients, path, arguments);
    }

    /**
     * {@inheritDoc}
     * (if path leads to a string list, a message will be delivered for each string)
     *
     * @param path configuration path to message pattern stored in base
     */
    @Override
    public void world(final World world, final String path, final Object... arguments) {
        final Recipients recipients = new WorldPlayers(world);
        this.submit(recipients, path, arguments);
    }

    /**
     * {@inheritDoc}
     * (if path leads to a string list, a message will be delivered for each string)
     *
     * @param path configuration path to message pattern stored in base
     */
    @Override
    public void publish(final String permission, final String path, final Object... arguments) {
        final Recipients recipients = new PermissionSubscribers(permission);
        this.submit(recipients, path, arguments);
    }

}
