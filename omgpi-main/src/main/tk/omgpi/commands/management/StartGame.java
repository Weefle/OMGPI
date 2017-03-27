package tk.omgpi.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.GameState;

/**
 * Start the game.
 */
public class StartGame extends OMGCommand {
    public StartGame() {
        super("startgame", "omgpi.manage", "start", "st");
    }

    public void call(CommandSender s, String label) {
        if (OMGPI.g.state != GameState.PRELOBBY) s.sendMessage(ChatColor.DARK_AQUA + "Game is already running.");
        else if (!OMGPI.g.countdown.isRunning || OMGPI.g.countdown.time > 10) {
            OMGPI.g.broadcast(ChatColor.AQUA + (s instanceof Player ? s.getName() : "Console") + " starts game...");
            OMGPI.g.countdown.rerun(10);
        }
    }
}
