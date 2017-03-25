package tk.omgpi.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.GameState;

public class SetTime extends OMGCommand {
    public SetTime() {
        super("settime", "omgpi.manage", "timeset", "tset", "sett");
    }

    public void call(CommandSender s, String label, String arg1) {
        if (OMGPI.g.state != GameState.INGAME) {
            s.sendMessage(ChatColor.RED + "Game is not running.");
            return;
        }
        try {
            int i = Integer.parseInt(arg1);
            OMGPI.g.broadcast(ChatColor.AQUA + (s instanceof Player ? s.getName() : "Console") + " set time to " + arg1 + ".");
            OMGPI.g.timerTicks = i;
        } catch (NumberFormatException e) {
            s.sendMessage("Cannot set time to " + arg1);
        }
    }
}
