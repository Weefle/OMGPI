package tk.omgpi.files;

import org.bukkit.ChatColor;
import tk.omgpi.OMGPI;
import tk.omgpi.utils.OMGList;

/**
 * map.omgc representation.
 */
public class Mapfig extends OMGConfig {
    /**
     * Associated map.
     */
    public OMGMap m;

    public Mapfig(OMGMap m) {
        super(m.dir, "map.omgc");
        this.m = m;
        if (setUnpresent("players", 8)) {
            setUnpresent("areas.0.coords", "0,0,0");
            setUnpresent("loots.OMGPI_Default", (Object) new String[]{"{id:dirt,Count:1,Probability:100}"});
        }
        setUnpresent("description", (Object) new String[]{"Default Description", "Plugin and/or map by staff"});
        setUnpresent("winMessage", "&3&l<---> %winner% &3&l<--->");
        OMGPI.g.event_preMapfigSave(this);
        save();
    }

    /**
     * Map description. Use for mapname, authors or something own
     *
     * @return description value
     */
    public OMGList<String> description() {
        return new OMGList<>(getStringList("description"));
    }

    /**
     * Win message. %winner% will be replaced with winner.
     *
     * @return winMessage value
     */
    public String winMessage() {
        return ChatColor.translateAlternateColorCodes('&', getString("winMessage"));
    }
}
