package tk.omgpi.commands.management;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.files.OMGKit;
import tk.omgpi.game.GameState;
import tk.omgpi.game.OMGPlayer;

public class SetKit extends OMGCommand {
    public SetKit() {
        super("setkit", "omgpi.manage", "skit", "skits", "skittles");
    }

    public void call(CommandSender s, String label, String arg1, String arg2) {
        if (OMGPI.g.state != GameState.INGAME) {
            s.sendMessage(ChatColor.DARK_AQUA + "Game is not running.");
            return;
        }
        if (!Bukkit.getPlayer(arg1).isOnline()) {
            s.sendMessage(ChatColor.DARK_AQUA + "Cannot find player " + arg1 + ".");
            return;
        }
        for (OMGKit k : OMGKit.kits)
            if (k.getName().equalsIgnoreCase(arg2)) {
                OMGPlayer.get((Player) s).setKit(k, false);
                s.sendMessage(ChatColor.DARK_AQUA + "Player " + arg1 + " kit has been set to " + arg2 + ".");
                return;
            }
        s.sendMessage(ChatColor.DARK_AQUA + "Cannot find kit \"" + arg2 + "\".");
    }
}
