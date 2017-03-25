package tk.omgpi.game;

public enum GameState {
    /**
     * Pre-game state.
     */
    PRELOBBY("Prelobby"),
    /**
     * Map discovery state (pre-start).
     */
    DISCOVERY("Discovery"),
    /**
     * In-game state.
     */
    INGAME("In Game"),
    /**
     * Post-game state.
     */
    ENDING("Ending"),
    /**
     * No game state, used for some sort of setups and freezes every OMGPI timing activity.
     */
    SETUPMODE("Setup Mode");

    public String s;

    GameState(String s) {
        this.s = s;
    }
}
