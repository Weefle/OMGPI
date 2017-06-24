package tk.omgpi.commands.management;

import org.bukkit.command.CommandSender;
import tk.omgpi.OMGPI;
import tk.omgpi.commands.OMGCommand;
import tk.omgpi.files.OMGKit;
import tk.omgpi.files.OMGLoot;
import tk.omgpi.game.OMGPlayer;
import tk.omgpi.game.OMGTeam;

/**
 * Show some info useful to developers.
 */
public class Debug extends OMGCommand {
    public Debug() {
        super("debug", "omgpi.manage");
    }

    public void call(CommandSender s, String label) {
        s.sendMessage("OMGPI debug: Use Minecraft logs if accessed by player and data didn't fit");
        s.sendMessage("name = " + OMGPI.g.name);
        s.sendMessage("state = " + OMGPI.g.state);
        s.sendMessage("isLootingOn = " + OMGPI.g.settings.isLootingOn);
        s.sendMessage("OMGLoot.loots = " + OMGLoot.loots);
        s.sendMessage("OMGKit.kits = " + OMGKit.kits);
        s.sendMessage("countLeaveAsKill = " + OMGPI.g.settings.countLeaveAsKill);
        s.sendMessage("allowHotbarEdit = " + OMGPI.g.settings.allowHotbarEdit);
        s.sendMessage("allowGameShop = " + OMGPI.g.settings.allowGameShop);
        s.sendMessage("allowIngameJoin = " + OMGPI.g.settings.allowIngameJoin);
        s.sendMessage("maxPlayers = " + OMGPI.g.settings.maxPlayers);
        s.sendMessage("gameLength = " + OMGPI.g.settings.gameLength);
        s.sendMessage("discoveryLength = " + OMGPI.g.settings.discoveryLength);
        s.sendMessage("hasDiscovery = " + OMGPI.g.settings.hasDiscovery);
        s.sendMessage("defaultTeam = " + OMGPI.g.defaultTeam);
        s.sendMessage("spectatorTeam = " + OMGPI.g.defaultTeam);
        s.sendMessage("gamePreparer = " + OMGPI.g.gamePreparer.getClass().getName());
        s.sendMessage("voteSystem.voting = " + OMGPI.g.voteSystem.voting);
        s.sendMessage("voteSystem.votes = " + OMGPI.g.voteSystem.votes);
        s.sendMessage("loadedMap = " + OMGPI.g.loadedMap);
        s.sendMessage("timerTicks = " + OMGPI.g.timerTicks);
        s.sendMessage("OMGTeam.registeredTeams = {");
        for (int i = 0; i < OMGTeam.registeredTeams.size(); i++) {
            OMGTeam t = OMGTeam.registeredTeams.get(i);
            s.sendMessage("team = " + t);
            s.sendMessage("state = " + t.state);
            s.sendMessage("prefix = " + t.prefix);
            s.sendMessage("displayName = " + t.displayName);
            s.sendMessage("allowFriendlyFire = " + t.allowFriendlyFire);
            s.sendMessage("gameMode = " + t.gameMode);
            s.sendMessage("hashdata = " + t.hashdata);
            if (i + 1 < OMGTeam.registeredTeams.size()) s.sendMessage(",");
        }
        s.sendMessage("}");
        s.sendMessage("OMGPlayer.link.values() = {");
        for (int i = 0; i < OMGPlayer.link.values().size(); i++) {
            OMGPlayer p = OMGPlayer.link.values().get(i);
            s.sendMessage("player = " + p);
            s.sendMessage("played = " + p.played);
            s.sendMessage("kit = " + p.kit);
            s.sendMessage("team = " + p.team);
            s.sendMessage("gameCoins = " + p.gameCoins);
            s.sendMessage("invulnerable = " + p.invulnerable);
            s.sendMessage("actionbar = " + p.actionbar);
            s.sendMessage("lastProjectileShotBy = " + p.lastProjectileShotBy);
            s.sendMessage("selectedHBESlot = " + p.selectedHBESlot);
            s.sendMessage("hashdata = " + p.hashdata);
            if (i + 1 < OMGPlayer.link.values().size()) s.sendMessage(",");
        }
        s.sendMessage("}");
    }
}
