package tk.omgpi.events.player;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import tk.omgpi.events.OMGEvent;
import tk.omgpi.game.OMGPlayer;

/**
 * Used in event_player_break();
 */
public class OMGBreakEvent extends OMGEvent implements Cancellable {
    public boolean cancel;
    /**
     * Player that broke the block
     */
    public OMGPlayer p;
    /**
     * Block that was broken
     */
    public Block b;

    public OMGBreakEvent(BlockBreakEvent bukkit, OMGPlayer p, Block b) {
        super(bukkit);
        this.p = p;
        this.b = b;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean b) {
        cancel = b;
    }
}
