package edgruberman.bukkit.playeractivity;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import edgruberman.bukkit.playeractivity.commands.Away;
import edgruberman.bukkit.playeractivity.commands.Back;
import edgruberman.bukkit.playeractivity.commands.Reload;
import edgruberman.bukkit.playeractivity.commands.Who;
import edgruberman.bukkit.playeractivity.consumers.AwayBack;
import edgruberman.bukkit.playeractivity.consumers.IdleKick;
import edgruberman.bukkit.playeractivity.consumers.IdleNotify;
import edgruberman.bukkit.playeractivity.consumers.ListTag;
import edgruberman.bukkit.playeractivity.messaging.ConfigurationCourier;
import edgruberman.bukkit.playeractivity.util.CustomPlugin;

public final class Main extends CustomPlugin {

    public IdleNotify idleNotify = null;
    public IdleKick idleKick = null;
    public AwayBack awayBack = null;
    public ListTag listTag = null;

    private ConfigurationCourier courier;

    @Override
    public void onLoad() { this.putConfigMinimum("config.yml", "3.0.0b19"); }

    @Override
    public void onEnable() {
        this.reloadConfig();
        this.courier = ConfigurationCourier.Factory.create(this).setBase("messages").build();

        PlayerMoveBlockEvent.MovementTracker.initialize(this);

        if (this.getConfig().getBoolean("idleNotify.enabled"))
            this.idleNotify = new IdleNotify(this, this.getConfig().getConfigurationSection("idleNotify"), this.courier, "playeractivity.idle.ignore.notify");

        if (this.getConfig().getBoolean("idleKick.enabled")) {
            this.idleKick = new IdleKick(this, this.getConfig().getConfigurationSection("idleKick"), this.courier, "playeractivity.idle.ignore.kick");
            if (this.idleNotify != null) this.idleNotify.idleKick = this.idleKick;
        }

        if (this.getConfig().getBoolean("listTag.enabled"))
            this.listTag = new ListTag(this, this.getConfig().getConfigurationSection("listTag"), this.courier, "playeractivity.idle.ignore.listtag");

        if (this.getConfig().getBoolean("awayBack.enabled")) {
            this.awayBack = new AwayBack(this, this.getConfig().getConfigurationSection("awayBack"), this.courier);
            if (this.awayBack.overrideIdle) {
                this.awayBack.idleNotify = this.idleNotify;
                if (this.idleNotify != null) this.idleNotify.awayBack = this.awayBack;
            }
            if (this.listTag != null) this.listTag.awayBack = this.awayBack;
            this.getCommand("playeractivity:away").setExecutor(new Away(this.courier, this.awayBack));
            this.getCommand("playeractivity:back").setExecutor(new Back(this.courier, this.awayBack));
        }

        this.getCommand("playeractivity:who").setExecutor(new Who(this, this.courier, this.awayBack, this.idleNotify, this.listTag));
        this.getCommand("playeractivity:reload").setExecutor(new Reload(this, this.courier));
    }

    @Override
    public void onDisable() {
        if (this.idleNotify != null) this.idleNotify.unload();
        if (this.idleKick != null) this.idleKick.unload();
        if (this.awayBack != null) this.awayBack.unload();
        if (this.listTag != null) this.listTag.unload();

        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);

        this.courier = null;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        this.courier.send(sender, "commandDisabled", label);
        return true;
    }

    public static String readableDuration(final long ms) {
        long total = TimeUnit.MILLISECONDS.toSeconds(ms);

        final long seconds = total % 60;
        total /= 60;
        final long minutes = total % 60;
        total /= 60;
        final long hours = total % 24;
        total /= 24;
        final long days = total;

        final StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(Long.toString(days)).append("d");
        if (hours > 0) sb.append((sb.length() > 0) ? " " : "").append(Long.toString(hours)).append("h");
        if (minutes > 0) sb.append((sb.length() > 0) ? " " : "").append(Long.toString(minutes)).append("m");
        if (seconds > 0) sb.append((sb.length() > 0) ? " " : "").append(Long.toString(seconds)).append("s");
        return sb.toString();
    }

}
