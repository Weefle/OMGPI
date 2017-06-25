package tk.omgpi.files;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import tk.omgpi.OMGPI;
import tk.omgpi.game.Area;
import tk.omgpi.game.OMGTeam;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A map
 */
public class OMGMap {
    /**
     * Map name defined by folder name
     */
    public String name;
    /**
     * Mapfig
     */
    public Mapfig mapfig;
    /**
     * Map directory (/plugins/OMGPI/games/GameName/maps/MapName/)
     */
    public File dir;

    /**
     * Create an OMGMap
     *
     * @param name Map name defined by folder name
     */
    public OMGMap(String name) {
        this.name = name;
        dir = new File(OMGPI.g.getDataFolder() + File.separator + "maps" + File.separator + name);
        mapfig = new Mapfig(this);
    }

    /**
     * Load the map and setup mapfig
     */
    public void load() {
        OMGPI.gameworld.unload();
        OMGTeam.registeredTeams.forEach(OMGTeam::setupMapfig);
        Area.registeredAreas.clear();
        if (mapfig.contains("areas")) mapfig.getConfigurationSection("areas").getKeys(false).forEach(Area::getAreaByID);
        OMGPI.g.settings.maxPlayers = mapfig.getInt("players", 8);
        try {
            FileUtils.copyDirectory(dir, new File(Bukkit.getWorldContainer() + "/gameworld"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When adding strings map name is used.
     *
     * @return Map name.
     */
    public String toString() {
        return name;
    }

    /**
     * Get random five maps from all visible (do not start with '.') maps.
     *
     * @return List of 5 maps.
     */
    @SuppressWarnings("ConstantConditions")
    public static List<String> getFiveMaps() {
        List<String> ss = getAllMaps().stream().filter(m -> !m.startsWith(".")).collect(Collectors.toList());
        Collections.shuffle(ss);
        if (ss.size() <= 0) return getAllMaps();
        return ss.stream().limit(5).collect(Collectors.toList());
    }

    /**
     * Get all possible maps.
     *
     * @return List of all maps.
     */
    @SuppressWarnings("ConstantConditions")
    public static List<String> getAllMaps() {
        List<String> ss = new LinkedList<>();
        if (OMGPI.g.mapsDirectory.listFiles() != null && OMGPI.g.mapsDirectory.listFiles().length != 0)
            ss = Arrays.stream(OMGPI.g.mapsDirectory.listFiles()).map(File::getName).collect(Collectors.toList());
        return ss;
    }
}
