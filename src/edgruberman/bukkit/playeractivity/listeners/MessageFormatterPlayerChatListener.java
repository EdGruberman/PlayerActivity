package edgruberman.bukkit.playeractivity.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import edgruberman.bukkit.messageformatter.PlayerChat;
import edgruberman.bukkit.playeractivity.EventListener;
import edgruberman.bukkit.playeractivity.Tracker;

public class MessageFormatterPlayerChatListener extends EventListener {

    public MessageFormatterPlayerChatListener(final Tracker tracker) {
        super(tracker);
        super.register(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(final PlayerChat event) {
        if (event.isCancelled()) return;

        this.record(event.getPlayer(), event);
    }

}
