package tk.omgpi.commands.management;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;

public class SetMap extends OMGCommand {
    public SetMap() {
        super("setmap", "omgpi.manage", "smap", "map", "maps", "mapset");
    }

    public void call(CommandSender s, String label, String arg1) {
        if (OMGPI.g.game_setMap(StringUtils.join(lastargscall, ' '))) {
            OMGPI.g.broadcast(ChatColor.AQUA + (s instanceof Player ? s.getName() : "Console") + " set map to " + StringUtils.join(lastargscall, ' ') + ".");
        } else s.sendMessage("Cannot set map to " + StringUtils.join(lastargscall, ' '));
    }
}
