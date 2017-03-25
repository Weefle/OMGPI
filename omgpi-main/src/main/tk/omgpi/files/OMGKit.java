package tk.omgpi.files;

import tk.omgpi.utils.OMGList;

import java.io.File;

/**
 * Default kit file.
 */
public class OMGKit extends OMGConfig {
    public static OMGList<OMGKit> kits = new OMGList<>();
    public static File dir;
    public String name;

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
