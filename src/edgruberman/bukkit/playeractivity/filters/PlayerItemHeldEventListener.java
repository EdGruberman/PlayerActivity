package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemHeldEvent;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PlayerItemHeldEventListener extends Interpreter {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(final PlayerItemHeldEvent event) {
        this.tracker.record(event.getPlayer(), event);
    }

}
