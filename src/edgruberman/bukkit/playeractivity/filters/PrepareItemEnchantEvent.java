package edgruberman.bukkit.playeractivity.filters;

import org.bukkit.event.EventHandler;

import edgruberman.bukkit.playeractivity.Interpreter;

public class PrepareItemEnchantEvent extends Interpreter {

    @EventHandler
    public void onEvent(final org.bukkit.event.enchantment.PrepareItemEnchantEvent event) {
        this.player = event.getEnchanter();
    }

}
