package tk.omgpi.files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import tk.omgpi.OMGPI;

import java.io.File;
import java.io.IOException;

/**
 * Custom YAML config in .omgc format
 */
public class OMGConfig extends YamlConfiguration {
    /**
     * Config file
     */
    public File omgc;

    /**
     * Create custom OMGConfig
     *
     * @param folder Folder to put file into.
     * @param name   File name. I'd suggest using .omgc on end.
     */
    public OMGConfig(File folder, String name) {
        omgc = new File(folder, name);
        reload();
        save();
    }

    /**
     * Reload configuration
     */
    public void reload() {
        try {
            load(omgc);
        } catch (IOException | InvalidConfigurationException e) {
            OMGPI.eLog("Cannot load " + omgc);
            e.printStackTrace();
        }
    }

    /**
     * Save configuration
     */
    public void save() {
        try {
            save(omgc);
        } catch (IOException e) {
            OMGPI.eLog("Cannot save " + omgc);
            e.printStackTrace();
        }
    }

    /**
     * Set object in config only if unpresent.
     *
     * @param path What to set. Separate compounds by dots.
     * @param value Value to set.
     * @return True if value was not present and hence set.
     */
    public boolean setUnpresent(String path, Object value) {
        if (!contains(path)) {
            set(path, value);
            return true;
        }
        return false;
    }

    /**
     * Set keys and values in config only if unpresent.
     *
     * @param path Root path for keys and values. Separate compounds by dots. Keys do not contain root path in them.
     * @param value Key, Value, Key, Value...
     * @return Array: Key, then boolean, true if each value was not present and hence set, nezt key...
     */
    public Object[] setUnpresent(String path, Object... value) {
        if (value.length % 2 == 1) throw new IllegalArgumentException("Amount of keys and values are unequal");
        Object[] o = new Object[value.length];
        String key = "";
        for (int i = 0; i < value.length; i++) {
            if (i % 2 == 0) {
                if (!(value[i] instanceof String)) throw new IllegalArgumentException("Key " + i + " is not a valid path.");
                o[i] = key = (String) value[i];
                continue;
            }
            if (!contains(path + "." + key)) {
                set(path, value);
                o[i] = true;
            } else o[i] = false;
        }
        return o;
    }
}
