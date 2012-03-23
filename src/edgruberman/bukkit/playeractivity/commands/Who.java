package edgruberman.bukkit.playeractivity.commands;

import org.bukkit.plugin.java.JavaPlugin;

import edgruberman.bukkit.playeractivity.commands.util.Handler;

public final class Who extends Handler {

    public static String list = null;
    public static String name = null;
    public static String delimiter = null;
    public static String connected = null;
    public static String disconnected = null;
    public static String awayTag = null;
    public static String away = null;
    public static String idleTag = null;
    public static String idle = null;

//  list: 'Who: %1$s' # 1 = Player List
//  name: '%s&_'
//  delimiter: ', '
//  connected: '%1$s&_ connected %2$s ago' # 1 = Player Display Name, 2 = Duration
//  disconnected: '%1$s&_ disconnected %2$s ago' # 1 = Player Name, 2 = Duration
//  away:
//      tag: "&6#Away&_"
//      format: ' and has been away %1$s for: %2$s&_' # 1 = Duration, 2 = Reason
//  idle:
//      tag: "&6#Idle&_"
//      format: ' and has been idle %1$s' # 1 = Duration

    public Who(final JavaPlugin plugin) {
        super(plugin, "who");
        new WhoStatus(this).setDefault();
        new WhoDetail(this);
    }

}
