package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class VehicleMoveEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.vehicle.VehicleMoveEvent event) {
        if (!(event.getVehicle().getPassenger() instanceof Player)) return;

        this.player = (Player) event.getVehicle().getPassenger();
    }

}
