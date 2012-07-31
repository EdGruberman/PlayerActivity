package edgruberman.bukkit.playeractivity.commands;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Messenger;
import edgruberman.bukkit.playeractivity.consumers.AwayBack;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;

public final class Away implements CommandExecutor {

    private final Messenger messenger;
    private final AwayBack awayBack;

    public Away(final Messenger messenger, final AwayBack awayBack) {
        this.messenger = messenger;
        this.awayBack = awayBack;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            this.messenger.tell(sender, "requiresPlayer");
            return true;
        }

        final Player player = (Player) sender;
        final AwayState state = this.awayBack.getAwayState(player);
        if (state != null) {
            this.messenger.tell(sender, "awayAlready", Main.readableDuration(System.currentTimeMillis() - state.since), (state.reason == null ? this.messenger.getFormat("awayDefaultReason") : state.reason));
            if (this.awayBack.mentions != null) this.awayBack.mentions.tellMentions(player);
            return true;
        }

        String reason = this.messenger.getFormat("+awayDefaultReason");
        if (args.length >= 1) reason = Away.join(Arrays.asList(args), " ");

        this.awayBack.setAway(player, reason);
        this.messenger.broadcast("awayBroadcast", player.getDisplayName(), reason);
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
