package tk.omgpi.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.files.OMGKit;
import tk.omgpi.game.OMGPlayer;

/**
 * Select a kit.
 */
public class RequestKit extends OMGCommand {
    public RequestKit() {
        super("kit", "omgpi.play", "requestkit", "rk");
    }

    public void call(CommandSender s, String label, String arg1) {
        if (!(s instanceof Player)) {
            OMGPI.wLog("Console can't ask for kits.");
            return;
        }
        for (OMGKit k : OMGKit.kits)
            if (k.getName().equalsIgnoreCase(arg1)) {
                OMGPI.g.player_request_kit(OMGPlayer.get((Player) s), k);
                return;
            }
        s.sendMessage(ChatColor.DARK_AQUA + "Cannot find team \"" + arg1 + "\".");
    }
}
