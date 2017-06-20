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

import java.util.*;
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
     * Allow explosion damage of specified blocks.
     */
    public Set<Material> canExplode;

    /**
     * For player of a team which is in the list: Right click blocks in area to open game shop.
     */
    public OMGList<OMGTeam> gameShop;

    /**
     * Allow team to break blocks.
     */
    public OMGHashMap<OMGTeam, Set<Material>> canBreak;

    /**
     * Allow team to place blocks.
     */
    public OMGHashMap<OMGTeam, Set<Material>> canPlace;

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
    public OMGHashMap<OMGTeam, Set<OMGDamageCause>> cancelDamage;

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
        canExplode = new HashSet<>();
        if (OMGPI.g.loadedMap.mapfig.contains("areas." + id + ".canExplode"))
            OMGPI.g.loadedMap.mapfig.getStringList("areas." + id + ".canExplode").forEach(cb -> {
                if (cb.equals("*")) canExplode.addAll(Arrays.asList(Material.values()));
                else if (cb.startsWith("-")) canExplode.remove(Material.matchMaterial(cb.substring(1)));
                else canExplode.add(Material.matchMaterial(cb));
            });
        OMGTeam.registeredTeams.forEach(t -> {
            String loc = OMGPI.g.loadedMap.mapfig.getString("areas." + id + "." + t + ".teleport");
            if (loc != null) teleport.put(t, parse(loc, ROTATION));
            String vec = OMGPI.g.loadedMap.mapfig.getString("areas." + id + "." + t + ".velocity");
            if (vec != null) velocity.put(t, parse(vec, POINT));
            if (OMGPI.g.loadedMap.mapfig.getBoolean("areas." + id + "." + t + ".gameShop", false))
                gameShop.add(t);
            Set<Material> canbreak = new HashSet<>();
            if (OMGPI.g.loadedMap.mapfig.contains("areas." + id + "." + t + ".canbreak"))
                OMGPI.g.loadedMap.mapfig.getStringList("areas." + id + "." + t + ".canbreak").forEach(cb -> {
                    if (cb.equals("*")) canbreak.addAll(Arrays.asList(Material.values()));
                    else if (cb.startsWith("-")) canbreak.remove(Material.matchMaterial(cb.substring(1)));
                    else canbreak.add(Material.matchMaterial(cb));
                });
            canBreak.put(t, canbreak);
            Set<Material> canplace = new HashSet<>();
            if (OMGPI.g.loadedMap.mapfig.contains("areas." + id + "." + t + ".canplace"))
                OMGPI.g.loadedMap.mapfig.getStringList("areas." + id + "." + t + ".canplace").forEach(cb -> {
                    if (cb.equals("*")) canplace.addAll(Arrays.asList(Material.values()));
                    else if (cb.startsWith("-")) canplace.remove(Material.matchMaterial(cb.substring(1)));
                    else canplace.add(Material.matchMaterial(cb));
                });
            canPlace.put(t, canplace);
            Set<OMGDamageCause> canceldamage = new HashSet<>();
            if (OMGPI.g.loadedMap.mapfig.contains("areas." + id + "." + t + ".canceldamage"))
                OMGPI.g.loadedMap.mapfig.getStringList("areas." + id + "." + t + ".canceldamage").forEach(cb -> {
                    if (cb.equals("*")) canceldamage.addAll(OMGDamageCause.values);
                    else if (cb.startsWith("-"))
                        canceldamage.remove(OMGDamageCause.valueOf(cb.substring(1).toUpperCase()));
                    else canceldamage.add(OMGDamageCause.valueOf(cb.toUpperCase()));
                });
            cancelDamage.put(t, canceldamage);
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