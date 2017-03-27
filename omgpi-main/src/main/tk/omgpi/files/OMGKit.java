package tk.omgpi.files;

import tk.omgpi.utils.OMGList;

import java.io.File;

/**
 * .kit file implementation.
 */
public class OMGKit extends OMGConfig {
    /**
     * Registered kits
     */
    public static OMGList<OMGKit> kits = new OMGList<>();
    /**
     * Kit folder (/plugins/OMGPI/games/GameName/kits/)
     */
    public static File dir;
    /**
     * Kit name
     */
    public String name;

    /**
     * Create a kit
     *
     * @param name Kit name
     */
    public OMGKit(String name) {
        super(dir, name + ".kit");
        this.name = name;
        kits.add(this);
    }

    /**
     * When adding strings kit name is used.
     */
    public String toString() {
        return name;
    }
}
