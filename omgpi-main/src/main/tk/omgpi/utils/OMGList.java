package tk.omgpi.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Custom LinkedList implementation with randomize and omgstream options.
 *
 * @param <E> Object Type
 */
public class OMGList<E> extends LinkedList<E> {
    /**
     * Create an empty list.
     */
    public OMGList() {
    }

    /**
     * Create a list containing all items from collection.
     *
     * @param list Any collection.
     */
    public OMGList(Collection<? extends E> list) {
        addAll(list);
    }

    /**
     * Randomize order of all objects in the list.
     *
     * @return Itself for building.
     */
    public OMGList randomize() {
        Collections.shuffle(this);
        return this;
    }

    /**
     * Get an omgstream for the list.
     *
     * @return An OMGStream.
     */
    public OMGStream<E> omgstream() {
        return new OMGStream<>(this);
    }
}
