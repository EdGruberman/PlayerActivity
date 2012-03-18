package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class EntityDamageByEntityEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        this.player = (Player) event.getDamager();
    }

}
