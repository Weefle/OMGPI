package tk.omgpi.files;

import org.bukkit.ChatColor;
import tk.omgpi.OMGPI;
import tk.omgpi.utils.OMGList;

public class Mapfig extends OMGConfig {
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

    public OMGList<String> description() {
        return new OMGList<>(getStringList("description"));
    }

    public String winMessage() {
        return ChatColor.translateAlternateColorCodes('&', getString("winMessage"));
    }
}
