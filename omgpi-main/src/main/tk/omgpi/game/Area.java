package tk.omgpi.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tk.omgpi.OMGPI;
import tk.omgpi.events.player.OMGDamageCause;
import tk.omgpi.utils.NBTParser;
import tk.omgpi.utils.OMGHashMap;
import tk.omgpi.utils.OMGList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static tk.omgpi.utils.Coordinates.CoordinateType.*;
import static tk.omgpi.utils.Coordinates.parse;

/**
 * Custom mapfig area object.
 */
public class Area {
    /**
     * HashMap linking ids to areas.
     */
    public static OMGHashMap<String, Area> registeredAreas = new OMGHashMap<>();

    /**
     * ID of the area
     */
    public String id;

    /**
     * Allow block burning.
     */
    public boolean allowBurn;

    /**
     * Allow liquid flow.
     */
    public boolean allowFlow;

    /**
     * Allow explosion damage.
     */
    public boolean allowExplosionDamage;

    /**
     * For player of a team which is in the list: Right click blocks in area to open game shop.
     */
    public OMGList<OMGTeam> gameShop;

    /**
     * Allow team to break blocks.
     */
    public OMGHashMap<OMGTeam, List<Material>> canBreak;

    /**
     * Allow team to place blocks.
     */
    public OMGHashMap<OMGTeam, List<Material>> canPlace;

    /**
     * Teleport team members when they enter the area.
     */
    public OMGHashMap<OMGTeam, double[]> teleport;

    /**
     * Push players when they enter the area.
     */
    public OMGHashMap<OMGTeam, double[]> velocity;

    /**
     * Cancel damage causes.
     */
    public OMGHashMap<OMGTeam, List<OMGDamageCause>> cancelDamage;

    /**
     * Give potion effects to the players in the area.
     */
    public OMGHashMap<OMGTeam, List<PotionEffect>> effects;

    /**
     * Area's cuboid.
     */
    public double[] coords;

    /**
     * Create unregistered area.
     */
    public Area() {
    }

    /**
     * Create an area using mapfig.
     *
     * @param id ID used in mapfig.
     */
    public Area(String id) {
        this.id = id;
        registeredAreas.put(id, this);
        gameShop = new OMGList<>();
        canBreak = new OMGHashMap<>();
        canPlace = new OMGHashMap<>();
        cancelDamage = new OMGHashMap<>();
        effects = new OMGHashMap<>();
        teleport = new OMGHashMap<>();
        velocity = new OMGHashMap<>();
        coords = parse(OMGPI.g.loadedMap.mapfig.getString("areas." + id + ".coords", "0,0,0"), AREA);
        allowBurn = OMGPI.g.loadedMap.mapfig.getBoolean("areas." + id + ".allowBurn", false);
        allowFlow = OMGPI.g.loadedMap.mapfig.getBoolean("areas." + id + ".allowFlow", false);
        allowExplosionDamage = OMGPI.g.loadedMap.mapfig.getBoolean("areas." + id + ".allowExplosionDamage", false);
        OMGTeam.registeredTeams.forEach(t -> {
            String loc = OMGPI.g.loadedMap.mapfig.getString("areas." + id + "." + t + ".teleport");
            if (loc != null) teleport.put(t, parse(loc, ROTATION));
            String vec = OMGPI.g.loadedMap.mapfig.getString("areas." + id + "." + t + ".velocity");
            if (vec != null) velocity.put(t, parse(vec, POINT));
            if (OMGPI.g.loadedMap.mapfig.getBoolean("areas." + id + "." + t + ".gameShop", false))
                gameShop.add(t);
            if (!OMGPI.g.loadedMap.mapfig.contains("areas." + id + "." + t + ".canBreak"))
                canBreak.put(t, new ArrayList<>());
            else if (!OMGPI.g.loadedMap.mapfig.getStringList("areas." + id + "." + t + ".canBreak").contains("*"))
                canBreak.put(t, OMGPI.g.loadedMap.mapfig.getStringList("areas." + id + "." + t + ".canBreak").stream().map(Material::matchMaterial).collect(Collectors.toList()));
            if (!OMGPI.g.loadedMap.mapfig.contains("areas." + id + "." + t + ".canPlace"))
                canPlace.put(t, new ArrayList<>());
            else if (!OMGPI.g.loadedMap.mapfig.getStringList("areas." + id + "." + t + ".canPlace").contains("*"))
                canPlace.put(t, OMGPI.g.loadedMap.mapfig.getStringList("areas." + id + "." + t + ".canPlace").stream().map(Material::matchMaterial).collect(Collectors.toList()));
            if (!OMGPI.g.loadedMap.mapfig.contains("areas." + id + "." + t + ".cancelDamage"))
                cancelDamage.put(t, new ArrayList<>());
            else if (!OMGPI.g.loadedMap.mapfig.getStringList("areas." + id + "." + t + ".cancelDamage").contains("*"))
                cancelDamage.put(t, OMGPI.g.loadedMap.mapfig.getStringList("areas." + id + "." + t + ".cancelDamage").stream().map(OMGDamageCause::valueOf).collect(Collectors.toList()));
            effects.put(t, !OMGPI.g.loadedMap.mapfig.contains("areas." + id + "." + t + ".effects") ? new ArrayList<>() : OMGPI.g.loadedMap.mapfig.getStringList("areas." + id + "." + t + ".effects").stream().map(m -> {
                NBTParser nbt = new NBTParser(m);
                return new PotionEffect(PotionEffectType.getByName(nbt.getString("id")), nbt.getInt("ticks"), nbt.getByte("level"), true, true);
            }).collect(Collectors.toList()));
        });
    }

    /**
     * Return area from id from config.
     *
     * @param id Given id.
     * @return Stored or new protected area, null if map not loaded or mapfig does not contain the map.
     */
    public static Area getAreaByID(String id) {
        if (OMGPI.g.loadedMap != null && OMGPI.g.loadedMap.mapfig.contains("areas." + id))
            return registeredAreas.containsKey(id) ? registeredAreas.get(id) : new Area(id);
        else return null;
    }

    /**
     * Check if there is material in allow break list.
     *
     * @param t FGAPI Team to check.
     * @param m Material to check
     * @return Result boolean.
     */
    public boolean isBreakAllowed(OMGTeam t, Material m) {
        return !canBreak.containsKey(t) || canBreak.get(t).contains(m);
    }

    /**
     * Check if there is material in allow place list.
     *
     * @param t FGAPI Team to check.
     * @param m Material to check
     * @return Result boolean.
     */
    public boolean isPlaceAllowed(OMGTeam t, Material m) {
        return !canPlace.containsKey(t) || canPlace.get(t).contains(m);
    }

    /**
     * Check if gameShop is true for the team.
     *
     * @param t FGAPI Team to check.
     * @return Result boolean.
     */
    public boolean isShop(OMGTeam t) {
        return gameShop.contains(t);
    }

    /**
     * Check if location is inside the area.
     *
     * @param loc Given location.
     * @return Result boolean.
     */
    public boolean isInside(Location loc) {
        return (coords[0] <= loc.getX() && loc.getX() <= coords[3]) && (coords[1] <= loc.getY() && loc.getY() <= coords[4]) && (coords[2] <= loc.getZ() && loc.getZ() <= coords[5]);
    }
}