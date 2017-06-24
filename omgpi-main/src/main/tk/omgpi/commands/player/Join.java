package tk.omgpi.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.GameState;
import tk.omgpi.game.OMGPlayer;

/**
 * Join currently running game (if game allows that)
 */
public class Join extends OMGCommand {
    public Join() {
        super("join", "omgpi.play", "play");
    }

    public void call(CommandSender s, String label) {
        if (!(s instanceof Player)) {
            OMGPI.wLog("Console can't join or leave.");
            return;
        }
        if (OMGPI.g.state != GameState.INGAME) {
            s.sendMessage(ChatColor.RED + "Game is not running.");
            return;
        }
        if (OMGPI.g.spectatorTeam.unpresent().size() < OMGPI.g.loadedMap.mapfig.getInt("players", 8))
            OMGPI.g.player_request_join(OMGPlayer.get((Player) s));
        else s.sendMessage(ChatColor.DARK_AQUA + "The game is full.");
    }
}
