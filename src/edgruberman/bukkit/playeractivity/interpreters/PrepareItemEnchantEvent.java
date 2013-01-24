package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class PrepareItemEnchantEvent extends Interpreter {

    public PrepareItemEnchantEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.enchantment.PrepareItemEnchantEvent.class);
    }

    @Override
    public void onExecute(final Event event) {
        final org.bukkit.event.enchantment.PrepareItemEnchantEvent sub = (org.bukkit.event.enchantment.PrepareItemEnchantEvent) event;
        if (!(sub.getEnchanter() instanceof Player)) return;

        this.record(sub.getEnchanter(), event);
    }

}
