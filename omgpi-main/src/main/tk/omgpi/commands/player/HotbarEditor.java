package tk.omgpi.commands.player;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.game.OMGPlayer;

public class HotbarEditor extends OMGCommand {
    public HotbarEditor() {
        super("hotbareditor", "omgpi.play", "hbe", "hotbaredit");
    }

    public void call(CommandSender s, String label) {
        if (!(s instanceof Player)) {
            OMGPI.wLog("Console can't open hotbar editor.");
            return;
        }
        if (!OMGPI.g.settings.allowHotbarEdit) {
            s.sendMessage(ChatColor.DARK_AQUA + "Hotbar editor is not allowed in this game.");
            return;
        }
        OMGPlayer.get((Player) s).hotbarEdit();
    }
}
