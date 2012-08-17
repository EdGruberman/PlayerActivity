package edgruberman.bukkit.playeractivity.messaging;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * individual {@link org.bukkit.command.CommandSender CommandSender}
 *
 * @author EdGruberman (ed@rjump.com)
 * @version 2.0.0
 */
public class Sender extends Recipients {

    protected CommandSender target;

    public Sender(final CommandSender target) {
        this.target = target;
    }

    @Override
    public Confirmation deliver(final Message message) {
        final String formatted = message.format(this.target).toString();
        this.target.sendMessage(formatted);
        return new Confirmation(this.level(), 1, "[SEND@{1}] {0}", message, Sender.this.target.getName());
    }

    /** console messages will be FINEST to allow for easier filtering of messages that will already appear in console */
    private Level level() {
        return (this.target instanceof Player ? Level.FINER : Level.FINEST);
    }

}
