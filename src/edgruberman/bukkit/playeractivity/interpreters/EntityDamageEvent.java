package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class EntityDamageEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.entity.EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        this.player = (Player) event.getEntity();
    }

}
