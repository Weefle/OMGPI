package tk.omgpi.events.player;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;
import tk.omgpi.OMGPI;
import tk.omgpi.events.OMGEvent;
import tk.omgpi.game.OMGPlayer;

public class OMGDamageEvent extends OMGEvent implements Cancellable {
    public boolean cancel;
    public OMGPlayer damaged;
    public Entity damager;
    public OMGDamageCause cause;
    public OMGDamageCause reason;
    public float damage;

    public OMGDamageEvent(EntityDamageEvent e, OMGPlayer damaged, Entity damager, OMGDamageCause reason, float damage) {
        super(e);
        cancel = false;
        this.damaged = damaged;
        this.damager = damager;
        this.reason = reason;
        this.damage = damage;
        OMGPI.g.event_player_damage(this);
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean b) {
        cancel = b;
        ((EntityDamageEvent) bukkit).setDamage(cancel ? 0 : damage);
    }

    /**
     * Set damage in both OMG and Bukkit events.
     */
    public void setDamage(float damage) {
        this.damage = damage;
        ((EntityDamageEvent) bukkit).setDamage(isCancelled() ? 0 : damage);
    }

    /**
     * Check if player becomes dead after event.
     * Equivalent to damaged.bukkit.getHealth() - ((EntityDamageEvent) bukkit).getFinalDamage() <= 0 || cause == OMGDamageCause.VOID.
     *
     * @return True if player is dead, false otherwise.
     */
    public boolean isDead() {
        return damaged.bukkit.getHealth() - ((EntityDamageEvent) bukkit).getFinalDamage() <= 0 || cause == OMGDamageCause.VOID;
    }
}
