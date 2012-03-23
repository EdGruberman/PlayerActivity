package edgruberman.bukkit.playeractivity.commands;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.playeractivity.Main;
import edgruberman.bukkit.playeractivity.commands.util.Action;
import edgruberman.bukkit.playeractivity.commands.util.Context;
import edgruberman.bukkit.playeractivity.commands.util.Handler;

final class WhoStatus extends Action {

    WhoStatus(final Handler handler) {
        super(handler, "status");
    }

    @Override
    public boolean perform(final Context context) {
        context.handler.command.getPlugin().onDisable();
        context.handler.command.getPlugin().onEnable();
        Main.messageManager.send(context.sender, "Configuration reloaded", MessageLevel.STATUS, false);
        return true;
    }

}
