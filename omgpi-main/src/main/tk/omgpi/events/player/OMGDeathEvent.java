package tk.omgpi.events.player;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import tk.omgpi.OMGPI;
import tk.omgpi.events.OMGEvent;
import tk.omgpi.game.OMGPlayer;

public class OMGDeathEvent extends OMGEvent implements Cancellable {
    public boolean cancel;
    public OMGDamageEvent damageEvent;
    public OMGPlayer damaged;
    public Entity damager;

    public OMGDeathEvent(OMGDamageEvent e) {
        super(e.bukkit);
        cancel = false;
        this.damageEvent = e;
        this.damaged = e.damaged;
        this.damager = e.damager;
        OMGPI.g.event_player_death(this);
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean b) {
        cancel = b;
    }

    /**
     * Send kill message to all players.
     */
    public void sendDeathMessage() {
        OMGPI.g.broadcast(damageEvent.reason.getDeathMessage(damaged));
    }
}
