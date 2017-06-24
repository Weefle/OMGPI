package tk.omgpi.game;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import tk.omgpi.OMGPI;
import tk.omgpi.utils.OMGList;

import java.util.List;
import java.util.function.Predicate;

public class OMGTeam extends Hashdatable {
    public static OMGList<OMGTeam> registeredTeams = new OMGList<>();
    public String id;
    public String displayName;
    public String prefix;
    public boolean allowFriendlyFire;
    public GameMode gameMode;
    public TeamState state;

    /**
     * Get filtered list of FishTeams.
     *
     * @param filter OMGTeam filter Predicate.
     * @return Filtered teams.
     */
    public static List<OMGTeam> getFiltered(Predicate<OMGTeam> filter) {
        return registeredTeams.omgstream().filter(filter).collect();
    }

    /**
     * Get OMGTeam by ID.
     *
     * @param id The ID.
     * @return OMGTeam.
     */
    public static OMGTeam getTeamByID(String id) {
        return registeredTeams.omgstream().filter(t -> t.id.equals(id)).findFirst().orElse(null);
    }

    /**
     * Create an OMGTeam.
     *
     * @param id ID used to get data from configs.
     * @param displayName Display name used to show the team.
     * @param prefix Prefix for every team member for chat and tab.
     * @param allowFriendlyFire Allow PVP in team.
     * @param gameMode GameMode that is set every tick.
     * @param players Players to add after team creation.
     */
    public OMGTeam(String id, String displayName, String prefix, boolean allowFriendlyFire, GameMode gameMode, OMGPlayer... players) {
        this.id = id;
        this.displayName = displayName;
        this.prefix = prefix;
        this.allowFriendlyFire = allowFriendlyFire;
        this.gameMode = gameMode;
        state = TeamState.UNSPECIFIED;
        registeredTeams.add(this);
        OMGPI.g.event_team_creation(this);
        for (OMGPlayer p : players) p.setTeam(this);
    }

    /**
     * Setup teams prefixes and display names.
     */
    public void setupMapfig() {
        if (OMGPI.g.loadedMap.mapfig.contains("teams." + id)) {
            displayName = ChatColor.translateAlternateColorCodes('&', OMGPI.g.loadedMap.mapfig.getString("teams." + id + ".name"));
            prefix = ChatColor.translateAlternateColorCodes('&', OMGPI.g.loadedMap.mapfig.getString("teams." + id + ".color"));
        }
    }

    /**
     * List all players in the team.
     *
     * @return List of players.
     */
    public OMGList<OMGPlayer> list() {
        return OMGPlayer.getFiltered(p -> p.team == this);
    }

    /**
     * List all players <b>not</b> in the team.
     *
     * @return List of players.
     */
    public OMGList<OMGPlayer> unpresent() {
        return OMGPlayer.getFiltered(p -> p.team != this);
    }

    /**
     * Check if player is in the team.
     *
     * @param p FishPlayer to check.
     * @return True if player team equals to this team, false otherwise.
     */
    public boolean contains(OMGPlayer p) {
        return p.team == this;
    }

    /**
     * Get amount of players in the team.
     *
     * @return Player amount in the team.
     */
    public int size() {
        return OMGPlayer.getFiltered(p -> p.team == this).size();
    }

    /**
     * Add player to the team.
     *
     * @param p Desired FishPlayer.
     */
    public void add(OMGPlayer p) {
        p.setTeam(this);
    }

    /**
     * Remove player from the team.
     *
     * @param p Desired FishPlayer.
     */
    public void remove(OMGPlayer p) {
        p.setTeam(null);
    }

    /**
     * Check if there are no players in the team.
     *
     * @return Players amount &lt;= 0.
     */
    public boolean isEmpty() {
        return size() <= 0;
    }

    /**
     * Remove all players from the team and the hashdata.
     */
    public void clear() {
        OMGPlayer.getFiltered(p -> p.team == this).forEach(p -> p.setTeam(null));
        hashdata.clear();
    }

    /**
     * Team states they can be in.
     */
    public enum TeamState {
        UNSPECIFIED, LOST, WON
    }

    public String toString() {
        return id;
    }

    /**
     * Check if there are any other teams than Spectator and Default.
     *
     * @return Teams size &gt; 2.
     */
    public static boolean anyElseRegistered() {
        return registeredTeams.size() > 2;
    }

    /**
     * Check if there is at least one team that won or there are no teams that haven't lost.
     *
     * @return Teams won &gt; 0 or Non spec teams that did not lose &lt; 1.
     */
    public static boolean somebodyWonOrEveryoneLost() {
        return getFiltered(t -> t.state == TeamState.WON).size() > 0 || getFiltered(t -> t != OMGPI.g.spectatorTeam && t.state != TeamState.LOST).size() < 1;
    }
}
