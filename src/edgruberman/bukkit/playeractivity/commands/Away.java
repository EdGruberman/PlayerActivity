package edgruberman.bukkit.playeractivity.commands;

import java.util.Collection;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Message;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;

public final class Away extends Executor {

    private final String defaultReason;

    public Away(final String defaultReason) {
        this.defaultReason = defaultReason;
    }

    @Override
    protected boolean execute(final CommandSender sender, final Command command, final String label, final List<String> args) {
        if (!(sender instanceof Player)) {
            Message.manager.tell(sender, "You must be a player in order to use this command", MessageLevel.SEVERE, false);
            return true;
        }

        final Player player = (Player) sender;
        final AwayState state = Main.awayBack.getAwayState(player);
        if (state != null) {
            Message.manager.tell(sender, "You have been away " + Main.duration(System.currentTimeMillis() - state.since) + (state.reason != null ? " for " + state.reason : ""), MessageLevel.SEVERE, false);
            if (Main.awayBack.mentions != null) Main.awayBack.mentions.tellMentions(player);
            return true;
        }

        String reason = this.defaultReason;
        if (args.size() >= 1) reason = Away.join(args, " ");

        Main.awayBack.setAway(player, reason);
        Message.manager.broadcast(String.format(Main.awayBack.awayFormat, player.getDisplayName(), reason), MessageLevel.EVENT);
        return true;
    }

    /**
     * Concatenate a collection with a delimiter.
     *
     * @param col entries to concatenate
     * @param delim placed between each entry
     * @return entries concatenated; empty string if no entries
     */
    private static String join(final Collection<? extends String> col, final String delim) {
        if (col == null || col.isEmpty()) return "";

        final StringBuilder sb = new StringBuilder();
        for (final String s : col) sb.append(s + delim);
        sb.delete(sb.length() - delim.length(), sb.length());

        return sb.toString();
    }

}
