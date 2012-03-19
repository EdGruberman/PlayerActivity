package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class InventoryCloseEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.inventory.InventoryCloseEvent event) {
        this.player = (Player) event.getPlayer();
    }

}
