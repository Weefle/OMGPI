package tk.omgpi.game;

import tk.omgpi.utils.OMGHashMap;

/**
 * Hashdatable is the object that stores hashdata - OMGHashMap&lt;String, Object&gt;.
 * Very useful because string can be really unique and allows easy access to objects.
 */
public class Hashdatable {
    /**
     * Hashmap to use if you need a set of keys or values.
     */
    public OMGHashMap<String, Object> hashdata;

    /**
     * Create a hashmap for the Hashdatable.
     */
    public Hashdatable() {
        hashdata = new OMGHashMap<>();
    }

    /**
     * Set value for the Hashdatable.
     *
     * @param key   Key to set.
     * @param value Value to set.
     * @return Previous value or null if was not present before.
     */
    public Object hashdata_set(String key, Object value) {
        return hashdata.put(key, value);
    }

    /**
     * Get value from the hashdata of Hashdatable.
     *
     * @param key Key to get.
     * @return Value to get and null if key was never set.
     */
    public Object hashdata_get(String key) {
        return hashdata.get(key);
    }
}
