package tk.omgpi.events.player;

import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import tk.omgpi.OMGPI;
import tk.omgpi.events.OMGEvent;
import tk.omgpi.game.OMGPlayer;

public class OMGJumpEvent extends OMGEvent {
    public OMGPlayer jumper;

    public OMGJumpEvent(PlayerStatisticIncrementEvent e, OMGPlayer p) {
        super(e);
        OMGPI.g.event_player_jump(this);
    }
}
