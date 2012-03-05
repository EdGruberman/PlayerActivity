package edgruberman.bukkit.playeractivity.filters.MessageFormatter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerChat extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final edgruberman.bukkit.messageformatter.PlayerChat event) {
        if (event.isCancelled()) return;

        this.tracker.record(event.getPlayer(), event);
    }

}
