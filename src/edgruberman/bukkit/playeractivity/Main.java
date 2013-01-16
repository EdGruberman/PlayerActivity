package edgruberman.bukkit.playeractivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
    public void onLoad() {
        this.putConfigMinimum("3.3.0a0");
        this.putConfigMinimum("language.yml", "3.3.0a0");
    }

    @Override
    public void onEnable() {
        this.reloadConfig();
        this.courier = ConfigurationCourier.Factory.create(this).setBase(this.loadConfig("language.yml")).setFormatCode("format-code").build();

        PlayerMoveBlockEvent.MovementTracker.initialize(this);

        ConfigurationSection section = this.getConfig().getConfigurationSection("idle-notify");
        if (section != null && section.getBoolean("enabled"))
            this.idleNotify = new IdleNotify(this, this.getIdle(section), this.getActivity(section), this.courier, "playeractivity.track.idlenotify");

        section = this.getConfig().getConfigurationSection("idle-kick");
        if (section != null && section.getBoolean("enabled")) {
            this.idleKick = new IdleKick(this, this.getIdle(section), this.getActivity(section), this.courier, "playeractivity.track.idlekick");
            if (this.idleNotify != null) this.idleNotify.idleKick = this.idleKick;
        }

        section = this.getConfig().getConfigurationSection("list-tag");
        if (section != null && section.getBoolean("enabled")) {
            this.listTag = new ListTag(this, this.getIdle(section), this.getActivity(section), this.courier, "playeractivity.track.listtag");
        }

        section = this.getConfig().getConfigurationSection("away-back");
        if (section != null && section.getBoolean("enabled")) {
            this.awayBack = new AwayBack(this, this.getActivity(section), section.getBoolean("override-idle"), section.getBoolean("mentions"), this.courier);
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
        this.courier.send(sender, "command-disabled", label);
        return true;
    }

    private List<String> getActivity(final ConfigurationSection section) {
        if (section.isList("activity")) return section.getStringList("activity");
        return this.getConfig().getStringList("activity");
    }

    private long getIdle(final ConfigurationSection section) {
        return TimeUnit.MILLISECONDS.convert(section.getInt("idle"), TimeUnit.SECONDS);
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
