package tk.omgpi.game;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;

/**
 * gameworld folder.
 */
public class GameWorld {
    /**
     * Changeable world instance.
     */
    public World bukkit;

    /**
     * Generator used to load this world.
     */
    public WorldCreator gen;

    /**
     * Struct.
     */
    public GameWorld() {
        gen = new WorldCreator("gameworld");
        gen.environment(World.Environment.NORMAL);
        gen.type(WorldType.FLAT);
        gen.generateStructures(false);
        gen.generatorSettings("2;0;1");
        unload();
    }

    /**
     * Unload world and delete folder for future maps.
     */
    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    public void unload() {
        if (new File(Bukkit.getWorldContainer() + File.separator + "gameworld").exists()) {
            load();
            Bukkit.unloadWorld(bukkit, false);
            try {
                FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer() + File.separator + "gameworld"));
            } catch (IOException e) {
                if (!e.getMessage().contains("Unable to delete")) e.printStackTrace();
            }
        }
    }

    /**
     * Load world.
     */
    public void load() {
        bukkit = gen.createWorld();
        bukkit.setPVP(true);
        bukkit.setDifficulty(Difficulty.NORMAL);
        bukkit.setGameRuleValue("doDaylightCycle", "false");
        bukkit.setGameRuleValue("doWeatherCycle", "false");
        bukkit.setGameRuleValue("showDeathMessages", "false");
        bukkit.setAutoSave(false);
    }

    /**
     * Using object in string addition replaces it with world name.
     *
     * @return gameworld
     */
    public String toString() {
        return "gameworld";
    }
}
