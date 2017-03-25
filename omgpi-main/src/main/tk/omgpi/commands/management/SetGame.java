package tk.omgpi.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SetGame extends OMGCommand {
    public SetGame() {
        super("setgame", "omgpi.manage", "setg", "gset", "gameset");
    }

    public void call(CommandSender s, String label) {
        OMGPI.mainfig.set("selectedGame", "random");
        OMGPI.mainfig.save();
        OMGPI.instance.reload();
    }

    public void call(CommandSender s, String label, String arg1) {
        if (arg1.equalsIgnoreCase("random")) {
            call(s, label);
            return;
        }
        List<String> games = new LinkedList<>();
        String[] list = new File(OMGPI.instance.getDataFolder() + File.separator + "games").list();
        if (list != null) Collections.addAll(games, list);
        games.removeIf(g -> !g.endsWith(".jar"));
        if (!games.contains(arg1 + ".jar"))
            s.sendMessage(ChatColor.DARK_AQUA + "Game " + arg1 + " not found in games folder.");
        else {
            OMGPI.mainfig.set("selectedGame", arg1);
            OMGPI.mainfig.save();
            OMGPI.instance.reload();
        }
    }
}
