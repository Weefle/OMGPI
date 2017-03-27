package tk.omgpi.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.GameState;

/**
 * Set game mode to setupmode, freezing all the timers.
 */
public class SetupMode extends OMGCommand {
    public SetupMode() {
        super("setupmode", "omgpi.manage", "sm");
    }

    public void call(CommandSender s, String label) {
        OMGPI.g.broadcast(ChatColor.AQUA + "Game set to setup mode by " + (s instanceof Player ? s.getName() : "Console") + ".");
        OMGPI.g.state = GameState.SETUPMODE;
    }
}
