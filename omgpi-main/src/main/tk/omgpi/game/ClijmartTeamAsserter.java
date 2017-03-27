package tk.omgpi.game;

import org.bukkit.GameMode;
import tk.omgpi.OMGPI;
import tk.omgpi.utils.OMGList;

import static tk.omgpi.OMGPI.g;
import static tk.omgpi.OMGPI.gameworld;

/**
 * Game preparer using Clijmart's team algorithm.
 * Also it is Default Game Preparer.
 */
public class ClijmartTeamAsserter implements GamePreparer {
    /**
     * The variable "t", as in team, shows ceil of max players divided by non-default teams amount.
     */
    public int t;
    /**
     * This stores teams with players &lt; t.
     */
    public OMGList<OMGTeam> smaller;

    public void player_start_discovery(OMGPlayer p) {
        p.play_sound_levelup();
        p.bukkit.setGameMode(GameMode.SPECTATOR);
        p.bukkit.teleport(gameworld.bukkit.getSpawnLocation());
    }

    public void player_start_nonDiscovery(OMGPlayer p) {
        p.play_sound_levelup();
        if (p.requestedTeam == g.spectatorTeam) {
            p.bukkit.setGameMode(GameMode.SPECTATOR);
            p.bukkit.teleport(gameworld.bukkit.getSpawnLocation());
        }
    }

    public void sortOutPlayers() {
        OMGList<OMGPlayer> sorted = g.spectatorTeam.list();
        int unspec = OMGPlayer.getFiltered(p -> p.requestedTeam != g.spectatorTeam).size();
        t = unspec == 2 || unspec < OMGTeam.registeredTeams.size() - 2 ? 1 : (int) Math.ceil(Math.min(g.settings.maxPlayers, unspec) / (OMGTeam.registeredTeams.size() - 2.0));
        smaller = new OMGList<>();
        OMGTeam.getFiltered(t -> t != g.defaultTeam && t != g.spectatorTeam).stream().sorted((o1, o2) -> {
            if (o1 == o2) return 0;
            if (OMGPlayer.getFiltered(p -> p.requestedTeam == o1).size() <= t) {
                smaller.add(o1);
                return -1;
            }
            return 1;
        }).forEach(t -> sorted.addAll(OMGPlayer.getFiltered(p -> p.requestedTeam == t)));
        sorted.addAll(g.defaultTeam.list());
        sorted.forEach(OMGPlayer::sendDescription);
        sorted.forEach(this::setupPlayer);
    }

    public void assertTeam(OMGPlayer p) {
        if (p.requestedTeam == g.spectatorTeam) return;
        if (g.spectatorTeam.unpresent().size() < g.loadedMap.mapfig.getInt("players", 8)) {
            if (!OMGTeam.anyElseRegistered() || smaller.contains(p.requestedTeam)) p.setTeam(p.requestedTeam);
            else if (p.requestedTeam == g.defaultTeam)
                p.setTeam(OMGTeam.getFiltered(t -> t != g.defaultTeam && t != g.spectatorTeam).stream().sorted((o1, o2) -> ((Integer) o1.size()).compareTo(o2.size())).findFirst().orElse(null));
            else if (p.requestedTeam.size() < t) p.setTeam(p.requestedTeam);
            else if (smaller.stream().filter(t -> t.size() < this.t).count() > 0)
                p.setTeam(smaller.stream().sorted((o1, o2) -> ((Integer) o1.size()).compareTo(o2.size())).findFirst().orElse(null));
        }
        g.event_player_assert(p);
    }

    public void setupPlayer(OMGPlayer p) {
        assertTeam(p);
        if (p.team != OMGPI.g.spectatorTeam) {
            g.player_giveKit(p);
            if (g.settings.allowGameShop) p.addGameCoins(5);
            p.bukkit.teleport(g.player_spawnLocation(p));
            p.played = true;
        }
    }

    public void setupPlayerNoAssert(OMGPlayer p) {
        if (p.team != OMGPI.g.spectatorTeam) {
            g.player_giveKit(p);
            if (g.settings.allowGameShop) p.addGameCoins(5);
            p.bukkit.teleport(g.player_spawnLocation(p));
            p.played = true;
        } else p.bukkit.teleport(OMGPI.gameworld.bukkit.getSpawnLocation().add(0.5, 0, 0.5));
    }
}
