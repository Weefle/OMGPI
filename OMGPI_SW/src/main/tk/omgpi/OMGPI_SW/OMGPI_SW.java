package tk.omgpi.OMGPI_SW;

import org.bukkit.GameMode;
import org.bukkit.Location;
import tk.omgpi.OMGPI;
import tk.omgpi.events.player.OMGDamageCause;
import tk.omgpi.events.player.OMGDeathEvent;
import tk.omgpi.files.Mapfig;
import tk.omgpi.game.Game;
import tk.omgpi.game.GameState;
import tk.omgpi.game.OMGPlayer;
import tk.omgpi.game.OMGTeam;
import tk.omgpi.utils.Coordinates;
import tk.omgpi.utils.OMGList;
import tk.omgpi.utils.ObjectiveBuffer;

/**
 * Easiest game to make on OMGPI.
 *
 * @author BurnyDaKath - OMGPI and the example game Skywars.
 */
public class OMGPI_SW extends Game {
    public static int j = 0;
    public static int pl = -1;

    public void onEnable() {
        name = "Skywars";
        super.onEnable();
        settings.hasDiscovery = false;
        settings.isLootingOn = true;
    }

    public Location player_spawnLocation(OMGPlayer p) {
        double[] coords = Coordinates.parse(loadedMap.mapfig.getStringList("spawns").get(++j), Coordinates.CoordinateType.ROTATION);
        return new Location(OMGPI.gameworld.bukkit(), coords[0], coords[1], coords[2], (float) (coords.length > 3 ? coords[3] : 0), (float) (coords.length > 3 ? coords[4] : 0));
    }

    public void event_preMapfigSave(Mapfig m) {
        m.setUnpresent("spawns", new OMGList<String>(){{
            for (int i = 1; i < m.getInt("players") + 1; i++) add("0,0,0");
        }});
    }

    public void game_checkForEnd() {
        bar_set();
        if (state == GameState.INGAME) {
            if (pl == -1) pl = defaultTeam.size();
            ObjectiveBuffer players = ObjectiveBuffer.createPlayerBuffer();
            OMGPlayer.link.values().forEach(p -> players.loadInto(p.displayObjective));
            player_updateScoreboardTeams();
            if (OMGPlayer.oneLeft()) {
                broadcast_win();
                game_stop();
            }
        }
    }

    public void event_team_creation(OMGTeam t) {
        if (t.id.equals("default")) t.gameMode = GameMode.SURVIVAL;
    }

    public void event_player_death(OMGDeathEvent e) {
        e.damaged.dropItems();
        e.damaged.setTeam(spectatorTeam);
        if (e.damageEvent.cause == OMGDamageCause.VOID)
            e.damaged.bukkit.teleport(e.damaged.bukkit.getWorld().getSpawnLocation());
        e.sendDeathMessage();
        e.damaged.played = false;
        player_reward(e.damaged, "loser");
        if (e.damaged.lastDamager != null && e.damaged.lastDamager != e.damaged) {
            e.damaged.lastDamager.play_sound_ding();
            player_reward(e.damaged.lastDamager, "kill");
        }
    }
}
