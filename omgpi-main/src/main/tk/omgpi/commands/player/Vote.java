package tk.omgpi.commands.player;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;

public class Vote extends OMGCommand {
    public Vote() {
        super("vote", "omgpi.play", "v");
    }

    public void call(CommandSender s, String label, String arg1) {
        if (!(s instanceof Player)) {
            OMGPI.wLog("Console can't vote.");
            return;
        }
        if (!OMGPI.g.voteSystem.voting) {
            s.sendMessage(ChatColor.DARK_AQUA + "Voting is not allowed right now.");
            return;
        }
        if (OMGPI.g.voteSystem.votes.keySet().size() == 1) {
            s.sendMessage(ChatColor.DARK_AQUA + "There is only one map available.");
            return;
        }
        if (OMGPI.g.voteSystem.vote((Player) s, StringUtils.join(lastargscall, ' '))) {
            s.sendMessage(ChatColor.DARK_AQUA + "Voted for " + StringUtils.join(lastargscall, ' '));
        } else s.sendMessage(ChatColor.DARK_AQUA + "Cannot vote for " + StringUtils.join(lastargscall, ' '));
    }
}
