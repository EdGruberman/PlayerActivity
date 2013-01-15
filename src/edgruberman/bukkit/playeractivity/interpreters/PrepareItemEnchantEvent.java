package edgruberman.bukkit.playeractivity.interpreters;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import edgruberman.bukkit.playeractivity.Interpreter;
import edgruberman.bukkit.playeractivity.StatusTracker;

public class PrepareItemEnchantEvent extends Interpreter {

    public PrepareItemEnchantEvent(final StatusTracker tracker) {
        super(tracker, org.bukkit.event.enchantment.PrepareItemEnchantEvent.class);
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        final org.bukkit.event.enchantment.PrepareItemEnchantEvent sub = (org.bukkit.event.enchantment.PrepareItemEnchantEvent) event;
        if (!(sub.getEnchanter() instanceof Player)) return;

        this.record(sub.getEnchanter(), event);
    }

}
