package edgruberman.bukkit.playeractivity.messaging;

import java.util.List;
import java.util.TimeZone;

import org.bukkit.command.CommandSender;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

/**
 * collection of one or more message targets
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 2.0.0
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

        return (TimeZone) values.get(0);
    }



    /** format and send message to each target */
    public abstract Confirmation deliver(Message message);

}
