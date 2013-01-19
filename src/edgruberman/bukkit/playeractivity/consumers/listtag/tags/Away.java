package edgruberman.bukkit.playeractivity.consumers.listtag.tags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.consumers.away.PlayerAway;
import edgruberman.bukkit.playeractivity.consumers.away.PlayerBack;
import edgruberman.bukkit.playeractivity.consumers.listtag.ListTag;
import edgruberman.bukkit.playeractivity.consumers.listtag.Tag;

public class Away extends Tag implements Listener {

    private final Map<String, String> reasons = new HashMap<String, String>();

    public Away(final ConfigurationSection config, final ListTag listTag, final Plugin plugin) {
        super(config, listTag, plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected void onUnload() {
        HandlerList.unregisterAll(this);
        this.reasons.clear();
    }

    @Override
    public String onDescribe(final Player player, final List<Object> arguments) {
        arguments.add(this.reasons.get(player.getName()));
        return super.onDescribe(player, arguments);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAway(final PlayerAway away) {
        this.reasons.put(away.getPlayer().getName(), away.getReason());
        this.attach(away.getPlayer(), away.getSince());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerBack(final PlayerBack back) {
        this.reasons.remove(back.getPlayer().getName());
        this.detach(back.getPlayer());
    }

}
