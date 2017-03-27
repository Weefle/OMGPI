package tk.omgpi.events;

import org.bukkit.event.Event;

/**
 * Custom OMGPI event
 */
public class OMGEvent {
    /**
     * Associated bukkit event. May be null.
     */
    public Event bukkit;

    /**
     * Struct.
     *
     * @param bukkit Associated bukkit event.
     */
    public OMGEvent(Event bukkit) {
        this.bukkit = bukkit;
    }
}
