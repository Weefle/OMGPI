package tk.omgpi.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.GameState;

public class SkipDiscovery extends OMGCommand {
    public SkipDiscovery() {
        super("skipdiscovery", "omgpi.manage", "sd", "skips");
    }

    public void call(CommandSender s, String label) {
        if (OMGPI.g.state != GameState.DISCOVERY) {
            s.sendMessage(ChatColor.DARK_AQUA + "Game is not in discovery mode.");
            return;
        }
        if (OMGPI.g.discoveryStartDelay == null) {
            s.sendMessage(ChatColor.DARK_AQUA + "Error: Discovery mode is on but there is no start delay task.");
            return;
        }
        OMGPI.g.discoveryStartDelay.cancel();
        OMGPI.g.game_readyToStart();
    }
}
