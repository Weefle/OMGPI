package tk.omgpi.game;

import org.bukkit.Bukkit;
import tk.omgpi.files.OMGKit;
import tk.omgpi.files.OMGLoot;

public class GameSettings {
    public Game game;
    public boolean allowIngameJoin;
    public boolean allowGameShop;
    public boolean allowKits;
    public boolean allowHotbarEdit;
    public boolean hasDiscovery;
    public boolean isLootingOn;
    public boolean countLeaveAsKill;
    public int maxPlayers;
    public int discoveryLength;
    public long gameLength;

    public GameSettings(Game g) {
        this.game = g;
        allowIngameJoin = false;
        allowGameShop = true;
        allowKits = OMGKit.kits.size() > 1;
        allowHotbarEdit = true;
        hasDiscovery = true;
        isLootingOn = OMGLoot.loots.size() > 0;
        countLeaveAsKill = true;
        maxPlayers = Bukkit.getServer().getMaxPlayers();
        discoveryLength = g.gamefig.getInt("discoveryLength", 500);
        gameLength = g.gamefig.getLong("gameLength", 1800);
    }
}
