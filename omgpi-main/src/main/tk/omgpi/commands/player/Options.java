package tk.omgpi.commands.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.OMGPlayer;

public class Options extends OMGCommand {
    public Options() {
        super("options", "omgpi.play");
    }

    public void call(CommandSender s, String label) {
        if (!(s instanceof Player)) {
            OMGPI.wLog("Console can't open options.");
            return;
        }
        OMGPlayer.get((Player) s).options();
    }
}
