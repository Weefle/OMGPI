package tk.omgpi.events.player;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockPlaceEvent;
import tk.omgpi.events.OMGEvent;
import tk.omgpi.game.OMGPlayer;

public class OMGPlaceEvent extends OMGEvent implements Cancellable {
    public boolean cancel;
    public OMGPlayer p;
    public Block b;

    public OMGPlaceEvent(BlockPlaceEvent bukkit, OMGPlayer p, Block b) {
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
