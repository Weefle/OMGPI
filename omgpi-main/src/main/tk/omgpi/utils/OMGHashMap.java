package tk.omgpi.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Sortable HashMap made by linking values with OMGList.
 *
 * @param <K> Key.
 * @param <V> Value.
 */
public class OMGHashMap<K, V> {
    /**
     * Hashmap used for value linking.
     */
    public HashMap<K, V> link;

    /**
     * Values used for sorting.
     */
    public OMGList<V> values;

    /**
     * Create a new OMGHashMap.
     */
    public OMGHashMap() {
        link = new HashMap<>();
        values = new OMGList<>();
    }

    /**
     * Randomize values in the hashmap.
     *
     * @return The object for building.
     */
    public OMGHashMap<K, V> randomize() {
        values.randomize();
        return this;
    }

    /**
     * Put a value into the hashmap.
     *
     * @param key Key
     * @param value Value
     * @return Previous object if there was any in the hashmap, null otherwise.
     */
    public V put(K key, V value) {
        if (!values.contains(value)) values.add(value);
        return link.put(key, value);
    }

    /**
     * Remove a value from the hashmap.
     *
     * @param key Key
     * @return Object that got removed or null.
     */
    public V remove(K key) {
        values.remove(link.get(key));
        return link.remove(key);
    }

    /**
     * Get an object from hashmap.
     *
     * @param key Key
     * @return Value
     */
    public V get(K key) {
        return link.get(key);
    }

    /**
     * Get a cloned (safe) list of values.
     *
     * @return OMGList of values.
     */
    public OMGList<V> values() {
        return new OMGList<>(values);
    }

    /**
     * Get a cloned (safe) set of keys.
     *
     * @return OMGList of keys.
     */
    public OMGList<K> keySet() {
        return new OMGList<>(link.keySet());
    }

    /**
     * Get a cloned (safe) set of entries.
     *
     * @return OMGList with unique values.
     */
    public OMGList<Map.Entry<K,V>> entrySet() {
        return new OMGList<>(link.entrySet());
    }

    /**
     * Remove all entries from hashmap.
     */
    public void clear() {
        link.clear();
        values.clear();
    }

    /**
     * Check if the key is in hashmap.
     *
     * @param key Key to check
     * @return True if there is a key
     */
    public boolean containsKey(K key) {
        return link.containsKey(key);
    }

    public String toString() {
        return link.toString();
    }
}
