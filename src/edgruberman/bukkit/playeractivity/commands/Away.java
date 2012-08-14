package edgruberman.bukkit.playeractivity.commands;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.consumers.AwayBack;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;
import edgruberman.bukkit.playeractivity.messaging.couriers.ConfigurationCourier;

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
            this.courier.send(sender, "requiresPlayer", label);
            return true;
        }

        final Player player = (Player) sender;
        final AwayState state = this.awayBack.getAwayState(player);
        if (state != null) {
            this.courier.send(sender, "awayAlready", Main.readableDuration(System.currentTimeMillis() - state.since), (state.reason == null ? this.courier.format("+awayDefaultReason") : state.reason));
            if (this.awayBack.mentions != null) this.awayBack.mentions.tellMentions(player);
            return true;
        }

        String reason = this.courier.format("+awayDefaultReason");
        if (args.length >= 1) reason = Away.join(Arrays.asList(args), " ");

        this.awayBack.setAway(player, reason);
        this.courier.broadcast("awayBroadcast", player.getDisplayName(), reason);
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
