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
import edgruberman.bukkit.playeractivity.consumers.AwayBack.AwayState;
import edgruberman.bukkit.playeractivity.messaging.ConfigurationCourier;

public class Mentions implements Listener {

    private final ConfigurationCourier courier;
    private final AwayBack awayBack;
    /** Player Name, Player Display Name, Time */
    private final Map<String, Map<String, Long>> mentions = new HashMap<String, Map<String, Long>>();

    public Mentions(final Plugin plugin, final ConfigurationCourier courier, final AwayBack awayBack) {
        this.courier = courier;
        this.awayBack = awayBack;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unload() {
        HandlerList.unregisterAll(this);
        this.mentions.clear();
    }

    public void tellMentions(final Player player) {
        if (!this.mentions.containsKey(player.getName())) return;

        final long now = System.currentTimeMillis();
        String mentions = "";
        for (final Map.Entry<String, Long> mention : this.mentions.get(player.getName()).entrySet()) {
            if (mentions.length() != 0) mentions += this.courier.format("mentions-summary.+delimiter");
            mentions += this.courier.format("mentions-summary.+player", mention.getKey(), Main.readableDuration(now - mention.getValue()));
        }

        this.courier.send(player, "mentions-summary.format", mentions);
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent chat) {
        final long now = System.currentTimeMillis();
        for (final String name : this.awayBack.getAway()) {
            final AwayState state = this.awayBack.getAwayState(name);
            final Player away = state.player();
            if (chat.getMessage().contains(away.getName()) || chat.getMessage().contains(away.getDisplayName())) {
                if (!this.mentions.containsKey(away.getName())) this.mentions.put(away.getName(), new HashMap<String, Long>());
                this.mentions.get(away.getName()).put(chat.getPlayer().getDisplayName(), now);
                this.courier.send(chat.getPlayer(), "mentions", away.getDisplayName(), Main.readableDuration(now - state.since), state.reason);
            }
        }
    }

    @EventHandler
    public void onPlayerBack(final PlayerBack back) {
        this.tellMentions(back.getPlayer());
        this.mentions.remove(back.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent quit) {
        this.mentions.remove(quit.getPlayer().getName());
    }

}
