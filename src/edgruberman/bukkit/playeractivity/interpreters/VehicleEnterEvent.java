package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class VehicleEnterEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.vehicle.VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;

        this.player = (Player) event.getEntered();
    }

}
