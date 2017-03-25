package tk.omgpi.game;

/**
 * Interface that manages players when game starts.
 */
public interface GamePreparer {
    /**
     * Called for each player before game_readyToStart() if discovery is on.
     */
    void player_start_discovery(OMGPlayer p);

    /**
     * Called for each player before game_readyToStart() if discovery is off.
     */
    void player_start_nonDiscovery(OMGPlayer p);

    /**
     * Set players teams, prepare them and everything related.
     */
    void sortOutPlayers();

    /**
     * Set players team and prepare them.
     */
    void assertTeam(OMGPlayer p);

    /**
     * Setup player when joining the game.
     */
    void setupPlayer(OMGPlayer p);

    /**
     * Setup player without calling assert team.
     */
    void setupPlayerNoAssert(OMGPlayer p);
}
