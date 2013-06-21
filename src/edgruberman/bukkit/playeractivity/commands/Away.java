package edgruberman.bukkit.playeractivity.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.consumers.away.AwayBack;
import edgruberman.bukkit.playeractivity.consumers.away.AwayBack.AwayState;
import edgruberman.bukkit.playeractivity.messaging.Courier.ConfigurationCourier;

public final class Away implements CommandExecutor {

    private final ConfigurationCourier courier;
    private final AwayBack awayBack;

    public Away(final ConfigurationCourier courier, final AwayBack awayBack) {
        this.courier = courier;
        this.awayBack = awayBack;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            this.courier.send(sender, "requires-player", label);
            return true;
        }

        final List<String> defaultReason = this.courier.format("away-default-reason");
        final String reason = ( args.length >= 1 ? Away.join(Arrays.asList(args), " ") : ( defaultReason.size() >= 1 ? defaultReason.get(0) : null ) );
        final Player player = (Player) sender;
        final AwayState state = this.awayBack.getAwayState(player.getName());
        if (state != null && (state.reason.equals(reason) || args.length == 0)) { // already away with same reason or no new reason
            this.courier.send(sender, "away-already", Main.readableDuration(System.currentTimeMillis() - state.since), (state.reason == null ? this.courier.format("away-default-reason") : state.reason));
            if (this.awayBack.mentions != null) this.awayBack.mentions.tellMentions(player);
            return true;
        }

        this.awayBack.setAway(player, reason);
        this.courier.broadcast("away", player.getDisplayName(), reason);
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
