package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PrepareItemEnchantEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.enchantment.PrepareItemEnchantEvent event) {
        this.player = event.getEnchanter();
    }

}
