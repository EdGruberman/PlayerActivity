package edgruberman.bukkit.playeractivity.consumers.listtag.tags;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.playeractivity.consumers.listtag.ListTag;
import edgruberman.bukkit.playeractivity.consumers.listtag.Tag;

public class Bed extends Tag implements Listener {

    public Bed(final ConfigurationSection config, final ListTag listTag, final Plugin plugin) {
        super(config, listTag, plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected void onUnload() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerBedEnter(final PlayerBedEnterEvent enter) {
        this.attach(enter.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerBedLeave(final PlayerBedLeaveEvent leave) {
        this.detach(leave.getPlayer());
    }

}
