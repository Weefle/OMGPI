package tk.omgpi.commands.management;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.GameState;
import tk.omgpi.game.OMGPlayer;
import tk.omgpi.game.OMGTeam;

public class SetTeam extends OMGCommand {
    public SetTeam() {
        super("setteam", "omgpi.manage", "steam", "teamset");
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
        for (OMGTeam t : OMGTeam.registeredTeams)
            if (t.id.equalsIgnoreCase(arg2) || ChatColor.stripColor(t.displayName).equalsIgnoreCase(arg2)) {
                OMGPI.g.player_set_team(OMGPlayer.get(Bukkit.getPlayer(arg1)), t);
                s.sendMessage(ChatColor.DARK_AQUA + "Player " + arg1 + " team has been set to " + arg2 + ".");
                return;
            }
        s.sendMessage(ChatColor.DARK_AQUA + "Cannot find team \"" + arg2 + "\".");
    }
}
