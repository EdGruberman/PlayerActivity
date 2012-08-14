package edgruberman.bukkit.playeractivity.messaging.couriers;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.messaging.Courier;
import edgruberman.bukkit.playeractivity.messaging.Message;
import edgruberman.bukkit.playeractivity.messaging.Recipients;
import edgruberman.bukkit.playeractivity.messaging.messages.TimestampedConfigurationMessage;

/**
 * handles message delivery and logging;
 * uses message patterns stored in a {@link org.bukkit.configuration.ConfigurationSection ConfigurationSection}
 * and {@link TimestampedConfigurationMessage}s to prepend a timestamp argument who's time zone is customized for each target
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 1.0.0
 */
public class TimestampedConfigurationCourier extends ConfigurationCourier {

    /**
     * create a {@link Courier} that uses message patterns stored in a specific section of the owning plugin's config.yml file
     * and prepends a timestamp argument who's time zone is customized for each target
     *
     * @param plugin owner
     * @param basePath path to section containing message patterns
     */
    public TimestampedConfigurationCourier(final Plugin plugin, final String basePath) {
        super(plugin, basePath);
    }

    /**
     * create a {@link Courier} that uses message patterns stored anywhere in the owning plugin's config.yml file
     * and prepends a timestamp argument who's time zone is customized for each target
     *
     * @param plugin owner
     */
    public TimestampedConfigurationCourier(final Plugin plugin) {
        super(plugin);
    }

    /**
     * create a {@link Courier} that uses message patterns in the supplied section
     * and prepends a timestamp argument who's time zone is customized for each target
     *
     * @param plugin owner
     * @param base section containing message patterns
     */
    public TimestampedConfigurationCourier(final Plugin plugin, final ConfigurationSection base) {
        super(plugin, base);
    }

    /**
     * {@inheritDoc}
     * prepend a timestamp argument who's time zone is customized for each target
     */
    @Override
    public void submit(final Recipients recipients, final String path, final Object... arguments) {
        final List<? extends Message> messages = TimestampedConfigurationMessage.create(this.getBase(), path, arguments);
        for (final Message message : messages)
            this.submit(recipients, message);

        if (messages.size() == 0)
            this.plugin.getLogger().log(Level.FINEST, "Message definition missing for {0}", path);
    }

}
