package tk.omgpi.files;

import tk.omgpi.utils.NBTParser;
import tk.omgpi.utils.OMGList;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * .loot file representation.
 */
public class OMGLoot extends OMGConfig {
    /**
     * Registered loots
     */
    public static OMGList<OMGLoot> loots = new OMGList<>();
    /**
     * Loot folder (/plugins/OMGPI/games/GameName/loots/)
     */
    public static File dir;
    /**
     * Loot name
     */
    public String name;
    /**
     * Loot contents
     */
    public LootParser contents;

    /**
     * Create a loot
     *
     * @param name Loot name
     */
    public OMGLoot(String name) {
        super(dir, name + ".loot");
        this.name = name;
        contents = new LootParser(getStringList("contents"));
        loots.add(this);
    }

    /**
     * When adding strings loot name is used.
     *
     * @return loot name.
     */
    public String toString() {
        return name;
    }

    /**
     * Get contents.
     *
     * @return contents.
     */
    public LootParser getContents() {
        return contents;
    }

    /**
     * Turn a list of NBTs into 2 lists for randomization.
     */
    public static class LootParser {
        /**
         * Loot contents, each list number corresponds to its id in the system.
         */
        public List<NBTParser> contents;
        /**
         * List of ids which can be randomized to get a random item.
         */
        public List<Integer> probabilities;

        /**
         * Parse a loot from nbt list. Probability INT tag is used for randomizing.
         *
         * @param nbts NBT strings list.
         */
        public LootParser(List<String> nbts) {
            contents = new LinkedList<>();
            probabilities = new LinkedList<>();
            nbts.forEach(mat -> contents.add(new NBTParser(mat)));
            contents.add(new NBTParser("{id:air,Probability:" + contents.size() * 50 + "}"));
            for (int j = 0; j < contents.size(); j++) {
                for (int i = 0; i < contents.get(j).c.getInt("Probability"); i++) {
                    probabilities.add(j);
                }
            }
        }

        /**
         * Get a random item based on 2 parsed lists.
         *
         * @return Random item.
         */
        public NBTParser getRandom() {
            return contents.get(probabilities.get(new Random().nextInt(probabilities.size())));
        }
    }

    /**
     * Get any loot contents by name or null if no loot found.
     *
     * @param name Loot name.
     * @return Loot contents of found loot or null.
     */
    public static LootParser nullFreeContents(String name) {
        OMGLoot n = OMGLoot.loots.stream().filter(l -> l.name.equals(name)).findFirst().orElse(null);
        return n == null ? null : n.getContents();
    }
}
