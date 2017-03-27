package tk.omgpi.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.OMGPlayer;

/**
 * Open game shop.
 */
public class GameShop extends OMGCommand {
    public GameShop() {
        super("gameshop", "omgpi.play", "shop");
    }

    public void call(CommandSender s, String label) {
        if (!(s instanceof Player)) {
            OMGPI.wLog("Console can't open game shop.");
            return;
        }
        if (OMGPlayer.get((Player) s).team == OMGPI.g.spectatorTeam) {
            s.sendMessage(ChatColor.RED + "Spectators cannot open shops.");
            return;
        }
        OMGPI.g.player_openGameShop(OMGPlayer.get((Player) s));
    }
}
