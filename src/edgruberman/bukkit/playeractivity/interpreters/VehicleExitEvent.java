package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class VehicleExitEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.vehicle.VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) return;

        this.player = (Player) event.getExited();
    }

}
