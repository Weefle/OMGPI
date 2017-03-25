package tk.omgpi.game;

import org.bukkit.Location;
import org.bukkit.block.Block;

import static tk.omgpi.OMGPI.mainfig;
import static tk.omgpi.utils.Coordinates.CoordinateType.AREA;
import static tk.omgpi.utils.Coordinates.parse;

/**
 * Class that manages game area.
 */
public class GameArea {
    /**
     * Check if given block is inside game area.
     */
    public static boolean isBlockInside(Block b) {
        Location loc = b.getLocation();
        double[] cds = parse(mainfig.getString("area"), AREA);
        return (cds[0] <= loc.getX() && loc.getX() <= cds[3]) && (cds[1] <= loc.getY() && loc.getY() <= cds[4]) && (cds[2] <= loc.getZ() && loc.getZ() <= cds[5]);
    }

    /**
     * Check if location is inside game area (Does not count height).
     */
    public static boolean isInside(Location loc) {
        double[] cds = parse(mainfig.getString("area"), AREA);
        return cds[0] <= loc.getX() && loc.getX() <= cds[3] && cds[2] <= loc.getZ() && loc.getZ() <= cds[5];
    }
}
