package tk.omgpi.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.OMGPlayer;
import tk.omgpi.game.OMGTeam;

/**
 * Select a team.
 */
public class RequestTeam extends OMGCommand {
    public RequestTeam() {
        super("team", "omgpi.play", "requestteam", "rt", "teams");
    }

    public void call(CommandSender s, String label, String arg1) {
        if (!(s instanceof Player)) {
            OMGPI.wLog("Console can't ask for teams.");
            return;
        }
        for (OMGTeam t : OMGTeam.registeredTeams)
            if (t.id.equalsIgnoreCase(arg1) || ChatColor.stripColor(t.displayName).equalsIgnoreCase(arg1)) {
                OMGPI.g.player_request_team(OMGPlayer.get((Player) s), t);
                return;
            }
        s.sendMessage(ChatColor.DARK_AQUA + "Cannot find team \"" + arg1 + "\".");
    }
}
