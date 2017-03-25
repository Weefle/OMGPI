package tk.omgpi.commands.management;

import org.bukkit.command.CommandSender;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.files.OMGLoot;

public class Debug extends OMGCommand {
    public Debug() {
        super("debug", "omgpi.manage");
    }

    public void call(CommandSender s, String label) {
        s.sendMessage("OMGPI debug");
        s.sendMessage("name = " + OMGPI.g.name);
        s.sendMessage("isLootingOn = " + OMGPI.g.settings.isLootingOn);
        OMGLoot.loots.forEach(l -> s.sendMessage("LOOT " + l));
        s.sendMessage("countLeaveAsKill = " + OMGPI.g.settings.countLeaveAsKill);
        s.sendMessage("allowHotbarEdit = " + OMGPI.g.settings.allowHotbarEdit);
        s.sendMessage("allowGameShop = " + OMGPI.g.settings.allowGameShop);
        s.sendMessage("allowIngameJoin = " + OMGPI.g.settings.allowIngameJoin);
    }
}
