package edgruberman.bukkit.playeractivity;

import java.util.HashMap;
import java.util.Map;

import edgruberman.bukkit.playeractivity.listeners.MessageFormatterPlayerChatListener;
import edgruberman.bukkit.playeractivity.listeners.PlayerChatEventListener;
import edgruberman.bukkit.playeractivity.listeners.PlayerDropItemEventListener;
import edgruberman.bukkit.playeractivity.listeners.PlayerInteractEventListener;
import edgruberman.bukkit.playeractivity.listeners.PlayerItemHeldEventListener;
import edgruberman.bukkit.playeractivity.listeners.PlayerJoinEventListener;
import edgruberman.bukkit.playeractivity.listeners.PlayerMoveEventListener;
import edgruberman.bukkit.playeractivity.listeners.PlayerToggleSneakEventListener;

public enum SupportedListeners {

      PLAYER_CHAT(PlayerChatEventListener.REFERENCE, PlayerChatEventListener.class)
    , PLAYER_DROP_ITEM(PlayerDropItemEventListener.REFERENCE, PlayerDropItemEventListener.class)
    , PLAYER_INTERACT(PlayerInteractEventListener.REFERENCE, PlayerInteractEventListener.class)
    , PLAYTER_ITEM_HELD(PlayerItemHeldEventListener.REFERENCE, PlayerItemHeldEventListener.class)
    , PLAYER_JOIN(PlayerJoinEventListener.REFERENCE, PlayerJoinEventListener.class)
    , PLAYER_MOVE(PlayerMoveEventListener.REFERENCE, PlayerMoveEventListener.class)
    , PLAYER_TOGGLE_SNEAK(PlayerToggleSneakEventListener.REFERENCE, PlayerToggleSneakEventListener.class)
    , MESSAGEFORMATTER_PLAYER_CHAT(MessageFormatterPlayerChatListener.REFERENCE, MessageFormatterPlayerChatListener.class)
    ;

    final String reference;
    final Class<? extends EventListener> listener;

    private SupportedListeners(final String reference, final Class<? extends EventListener> listener) {
        this.reference = reference;
        this.listener = listener;
    }

    private static final Map<String, Class<? extends EventListener>> mapping = new HashMap<String, Class<? extends EventListener>>();

    static {
        for (SupportedListeners supportedListener : SupportedListeners.values())
            SupportedListeners.mapping.put(supportedListener.reference, supportedListener.listener);
    }

    public static final Class<? extends EventListener> getListenerFor(final String reference) {
        return SupportedListeners.mapping.get(reference);
    }

}