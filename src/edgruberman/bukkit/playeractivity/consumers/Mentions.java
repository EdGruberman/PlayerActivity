package edgruberman.bukkit.playeractivity.consumers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;

public class Mentions implements Listener {

    private final Plugin plugin;
    private final AwayBack awayBack;
    private final Map<Player, Map<Player, Long>> mentions = new HashMap<Player, Map<Player, Long>>();

    public Mentions(final Plugin plugin, final AwayBack awayBack) {
        this.plugin = plugin;
        this.awayBack = awayBack;
    }

    public void start() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        this.mentions.clear();
    }

    public void tellMentions(final Player player) {
        if (!this.mentions.containsKey(player)) return;

        final long now = System.currentTimeMillis();
        String mentions = "";
        for (final Map.Entry<Player, Long> mention : this.mentions.get(player).entrySet()) {
            if (mentions.length() != 0) mentions += ", ";
            mentions += "&f" + mention.getKey().getDisplayName() + "&_ (" + Main.duration(now - mention.getValue()) + ")";
        }

        final String mentionedBy = String.format("Your name was mentioned by: %s", mentions);
        MessageManager.of(this.plugin).tell(player, mentionedBy, MessageLevel.STATUS);
    }

    @EventHandler
    public void onPlayerChat(final PlayerChatEvent event) {
        final long now = System.currentTimeMillis();
        for (final Player away : this.awayBack.getAway()) {
            if (event.getMessage().contains(away.getName())) {
                if (!this.mentions.containsKey(away)) this.mentions.put(away, new HashMap<Player, Long>());
                this.mentions.get(away).put(event.getPlayer(), now);

                final AwayState state = Main.awayBack.getAwayState(away);
                final String response = String.format(this.awayBack.mentionsFormat, state.player.getDisplayName(), Main.duration(now - state.since), state.reason);
                MessageManager.of(this.plugin).tell(event.getPlayer(), response, MessageLevel.STATUS);
            }
        }
    }

    @EventHandler
    public void onPlayerBack(final PlayerBack event) {
        this.tellMentions(event.getPlayer());
        this.mentions.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.mentions.remove(event.getPlayer());
    }

}
