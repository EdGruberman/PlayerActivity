package edgruberman.bukkit.playeractivity.consumers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.Messenger;
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;

public class Mentions implements Listener {

    private final Messenger messenger;
    private final AwayBack awayBack;
    private final Map<Player, Map<Player, Long>> mentions = new HashMap<Player, Map<Player, Long>>();

    public Mentions(final Plugin plugin, final Messenger messenger, final AwayBack awayBack) {
        this.messenger = messenger;
        this.awayBack = awayBack;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unload() {
        HandlerList.unregisterAll(this);
        this.mentions.clear();
    }

    public void tellMentions(final Player player) {
        if (!this.mentions.containsKey(player)) return;

        final long now = System.currentTimeMillis();
        String mentions = "";
        for (final Map.Entry<Player, Long> mention : this.mentions.get(player).entrySet()) {
            if (mentions.length() != 0) mentions += this.messenger.getFormat("mentionsSummary.+delimiter");
            mentions += String.format(this.messenger.getFormat("mentionsSummary.+player"), mention.getKey().getDisplayName(), Main.readableDuration(now - mention.getValue()));
        }

        this.messenger.tell(player, "mentionsSummary.format", mentions);
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent chat) {
        final long now = System.currentTimeMillis();
        for (final Player away : this.awayBack.getAway()) {
            if (chat.getMessage().contains(away.getName())) {
                if (!this.mentions.containsKey(away)) this.mentions.put(away, new HashMap<Player, Long>());
                this.mentions.get(away).put(chat.getPlayer(), now);

                final AwayState state = this.awayBack.getAwayState(away);
                this.messenger.tell(chat.getPlayer(), "mentions", state.player.getDisplayName(), Main.readableDuration(now - state.since), state.reason);
            }
        }
    }

    @EventHandler
    public void onPlayerBack(final PlayerBack back) {
        this.tellMentions(back.getPlayer());
        this.mentions.remove(back.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent quit) {
        this.mentions.remove(quit.getPlayer());
    }

}
