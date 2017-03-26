package tk.omgpi.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.GameState;
import tk.omgpi.game.OMGPlayer;

public class Spectate extends OMGCommand {
    public Spectate() {
        super("spectate", "omgpi.play", "spec");
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
        OMGPI.g.player_request_spectate(OMGPlayer.get((Player) s));
    }
}
