package edgruberman.bukkit.playeractivity.commands;

import edgruberman.bukkit.playeractivity.commands.util.Action;
import edgruberman.bukkit.playeractivity.commands.util.Context;
import edgruberman.bukkit.playeractivity.commands.util.Handler;

final class WhoDetail extends Action {

    WhoDetail(final Handler handler) {
        super(handler, "detail");
    }

    @Override
    public boolean perform(final Context context) {
        // TODO add actions here
        return true;
    }

}
