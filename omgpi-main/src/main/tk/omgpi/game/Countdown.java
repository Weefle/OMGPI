package tk.omgpi.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import tk.omgpi.OMGPI;

/**
 * Game counter before game.
 */
public class Countdown extends BukkitRunnable {
    /**
     * Ticks left before game starts.
     */
    public int time;
    /**
     * To check if countdown is running.
     */
    public boolean isRunning;

    /**
     * Struct.
     */
    public Countdown() {
        super();
        time = 0;
        isRunning = false;
        OMGPI.g.countdown = this;
    }

    /**
     * A countdown tick.
     */
    public void run() {
        if (OMGPI.g.state != GameState.PRELOBBY) {
            cancel();
            return;
        }
        if (time > 60 && time % 60 == 0) {
            OMGPI.g.broadcast(ChatColor.AQUA + "Game starts in " + time / 60 + " minutes...");
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1f));
        }
        if (time == 60) {
            OMGPI.g.broadcast(ChatColor.AQUA + "Game starts in 1 minute...");
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1f));
        }
        if (time == 30) {
            OMGPI.g.broadcast(ChatColor.AQUA + "Game starts in 30 seconds...");
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1.15f));
        }
        if (time == 15) {
            OMGPI.g.broadcast(ChatColor.AQUA + "Game starts in 15 seconds...");
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1.25f));
        }
        if (time == 10) {
            OMGPI.g.broadcast(ChatColor.AQUA + "Game starts in 10 seconds...");
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1.5f));
            OMGPI.g.voteSystem.stop(null);
        }
        if (time <= 5) {
            OMGPI.g.broadcast(ChatColor.AQUA + "Game starts in " + time + " seconds...");
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1.5f + (0.5f - time / 10f)));
        }
        if (time == 0) {
            cancel();
            OMGPI.gameworld.load();
            new BukkitRunnable() {
                public void run() {
                    OMGPI.g.game_start();
                }
            }.runTaskLater(OMGPI.instance, 20);
        }
        OMGPI.g.bar_set();
        time--;
    }

    /**
     * Start counting. Do not use if some countdown is already working. Will register a new countdown.
     *
     * @param s Starting position of counter.
     */
    public void start(int s) {
        isRunning = true;
        time = s;
        runTaskTimer(OMGPI.instance, 0, 20);
        OMGPI.g.countdown = this;
    }

    /**
     * Rerun counter. A better alternative to using start() as it checks if any countdown is already working.
     *
     * @param s Starting position of counter.
     */
    public void rerun(int s) {
        if (isRunning) cancel();
        OMGPI.g.countdown.start(s);
    }

    /**
     * Stop counting.
     */
    public void cancel() {
        super.cancel();
        OMGPI.g.countdown = new Countdown();
    }
}
