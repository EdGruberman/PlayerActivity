package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class InventoryOpenEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.inventory.InventoryOpenEvent event) {
        this.player = (Player) event.getPlayer();
    }

}
