package edgruberman.bukkit.messaging.recipients;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import edgruberman.bukkit.messaging.Message;
import edgruberman.bukkit.messaging.Recipients;
import edgruberman.bukkit.messaging.messages.Confirmation;

public class PermissionSubscribers implements Recipients {

    protected String permission;

    public PermissionSubscribers(final String permission) {
        this.permission = permission;
    }

    @Override
    public Confirmation send(final Message message) {
        int count = 0;
        for (final Permissible permissible : Bukkit.getPluginManager().getPermissionSubscriptions(this.permission))
            if (permissible instanceof CommandSender && permissible.hasPermission(this.permission)) {
                final CommandSender target = (CommandSender) permissible;
                target.sendMessage(message.formatFor(target));
                count++;
            }

        return new PermissionSubscribersConfirmation(message, count);
    }



    public class PermissionSubscribersConfirmation extends Confirmation {

        public PermissionSubscribersConfirmation(final Message message, final int count) {
            super(Level.FINER, count, "[PUBLISH@%2$s(%3$d)] %1$s", message, PermissionSubscribers.this.permission, count);
        }

    }

}
