package edgruberman.bukkit.playeractivity.consumers.away;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.consumers.away.AwayBack.AwayState;
import edgruberman.bukkit.playeractivity.messaging.ConfigurationCourier;
import edgruberman.bukkit.playeractivity.util.JoinList;

public class Mentions implements Listener {

    private final Plugin plugin;
    private final ConfigurationCourier courier;
    private final AwayBack awayBack;
    /** Player Name, Player Display Name, Time */
    private final Map<String, Map<String, Long>> mentions = new HashMap<String, Map<String, Long>>();

    public Mentions(final Plugin plugin, final ConfigurationCourier courier, final AwayBack awayBack) {
        this.plugin = plugin;
        this.courier = courier;
        this.awayBack = awayBack;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void unload() {
        HandlerList.unregisterAll(this);
        this.mentions.clear();
    }

    public void tellMentions(final Player player) {
        if (!this.mentions.containsKey(player.getName())) return;

        final long now = System.currentTimeMillis();
        final JoinList mentions = new JoinList(this.courier.getBase().getConfigurationSection("mentions-summary"));
        for (final Map.Entry<String, Long> mention : this.mentions.get(player.getName()).entrySet())
            mentions.add(mention.getKey(), Main.readableDuration(now - mention.getValue()));

        this.courier.send(player, "mentions-summary.format", mentions.toString());
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent chat) {
        Bukkit.getScheduler().runTask(this.plugin, new CheckMentions(chat.getPlayer(), chat.getMessage(), System.currentTimeMillis()));
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

    private class CheckMentions implements Runnable {

        private final Player chatter;
        private final String message;
        private final long sent;

        private CheckMentions(final Player chatter, final String message, final long sent) {
            this.chatter = chatter;
            this.message = message;
            this.sent = sent;
        }

        @Override
        public void run() {
            for (final String name : Mentions.this.awayBack.getAway()) {
                if (this.chatter.getName().equals(name)) continue; // ignore players mentioning their own name while they are away
                final AwayState state = Mentions.this.awayBack.getAwayState(name);
                final Player away = state.player();
                if (this.message.contains(away.getName()) || this.message.contains(away.getDisplayName())) {
                    if (!Mentions.this.mentions.containsKey(away.getName())) Mentions.this.mentions.put(away.getName(), new HashMap<String, Long>());
                    Mentions.this.mentions.get(away.getName()).put(this.chatter.getDisplayName(), this.sent);
                    Mentions.this.courier.send(this.chatter, "mentions", away.getDisplayName(), Main.readableDuration(this.sent - state.since), state.reason);
                }
            }
        }

    }

}
