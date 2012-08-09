package edgruberman.bukkit.messaging.recipients;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.entity.Player;

import edgruberman.bukkit.messaging.Message;
import edgruberman.bukkit.messaging.Recipients;
import edgruberman.bukkit.messaging.messages.Confirmation;

public class WorldPlayers implements Recipients {

    protected final World world;

    public WorldPlayers(final World world) {
        this.world = world;
    }

    @Override
    public WorldConfirmation send(final Message message) {
        final List<Player> players = this.world.getPlayers();
        for (final Player player : players)
                player.sendMessage(message.formatFor(player));

        return new WorldConfirmation(message.toString(), players.size());
    }



    public class WorldConfirmation extends Confirmation {

        public WorldConfirmation(final String message, final int count) {
            super(Level.FINER, count, "[WORLD%%%2$s(%3$d)] %1$s", message, WorldPlayers.this.world.getName(), count);

        }

    }

}
