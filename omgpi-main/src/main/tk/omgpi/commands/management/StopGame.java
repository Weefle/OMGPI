package tk.omgpi.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.GameState;

public class StopGame extends OMGCommand {
    public StopGame() {
        super("stopgame", "omgpi.manage", "sg", "gamestop", "gs");
    }

    public void call(CommandSender s, String label) {
        if (OMGPI.g.state != GameState.INGAME) s.sendMessage(ChatColor.DARK_AQUA + "Game is not running.");
        else {
            OMGPI.g.broadcast(ChatColor.AQUA + (s instanceof Player ? s.getName() : "Console") + " stops game...");
            OMGPI.g.game_stop();
        }
    }
}
