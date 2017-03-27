package tk.omgpi.game;

import net.minecraft.server.v1_11_R1.*;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;
import tk.omgpi.OMGPI;
import tk.omgpi.files.OMGKit;
import tk.omgpi.utils.NBTParser;
import tk.omgpi.utils.OMGHashMap;
import tk.omgpi.utils.OMGList;

import java.util.function.Predicate;

import static org.bukkit.ChatColor.AQUA;

/**
 * OMGPI Custom player.
 */
public class OMGPlayer extends Hashdatable {
    /**
     * Registered players
     */
    public static OMGHashMap<Player, OMGPlayer> link = new OMGHashMap<>();
    /**
     * Associated bukkit player
     */
    public Player bukkit;
    /**
     * Currently selected kit
     */
    public OMGKit kit;
    /**
     * Selected Hotbar Editor slot, -1 for none
     */
    public int selectedHBESlot;
    /**
     * Amount of game coins that player has
     */
    public int gameCoins;
    /**
     * Currently shown action bar
     */
    public String actionbar;
    /**
     * Player's last damager
     */
    public OMGPlayer lastDamager;
    /**
     * Player's team. If null can cause some issues
     */
    public OMGTeam team;
    /**
     * Player's requested team
     */
    public OMGTeam requestedTeam;
    /**
     * Custom checks if player is invulnerable (So there is no conflicts with other plugins)
     */
    public boolean invulnerable;
    /**
     * Last projectile that hit the player
     */
    public Projectile lastProjectileShotBy;
    /**
     * Objective used for sidebar display
     */
    public Objective displayObjective;
    /**
     * Whether player has ever played the game or not (for safe checks of winner)
     */
    public boolean played;

    /**
     * Get filtered list of registered players.
     *
     * @param filter p -&gt; boolean
     * @return Filtered list.
     */
    public static OMGList<OMGPlayer> getFiltered(Predicate<OMGPlayer> filter) {
        return link.values().omgstream().filter(filter).collect();
    }

    /**
     * Create a new OMGPlayer.
     *
     * @param p Bukkit player to link.
     */
    public OMGPlayer(Player p) {
        bukkit = p;
        link.put(bukkit, this);
        OMGPI.g.player_addToSQL(this);
        fullReset();
        selectWorld();
    }

    /**
     * Remove player from all teams, unvote and unlink bukkit player.
     */
    public void remove() {
        setTeam(null);
        OMGPI.g.voteSystem.votes.keySet().forEach(m -> OMGPI.g.voteSystem.votes.get(m).remove(this));
        link.remove(bukkit);
    }

    /**
     * Update issued on every tick of game module.
     */
    public void update() {
        if (invulnerable) bukkit.setFireTicks(0);
        if (OMGPI.g.checkStates(GameState.DISCOVERY, GameState.INGAME, GameState.ENDING) && team != null) {
            bukkit.setDisplayName(team.prefix + bukkit.getName());
            if (bukkit.getGameMode() != team.gameMode) bukkit.setGameMode(team.gameMode);
            if (team != OMGPI.g.spectatorTeam) bukkit.setCompassTarget(OMGPI.g.player_compassLocation(this));
            Area.registeredAreas.values().stream().filter(a -> a.isInside(bukkit.getLocation().getBlock().getLocation())).forEach(a -> {
                double[] vds = a.velocity.get(team);
                if (vds != null) bukkit.setVelocity(new Vector(vds[0], vds[1], vds[2]));
                double[] cds = a.teleport.get(team);
                if (cds != null)
                    bukkit.teleport(new Location(OMGPI.gameworld.bukkit, cds[0], cds[1], cds[2], (float) (cds.length > 3 ? cds[3] : 0), (float) (cds.length > 3 ? cds[4] : 0)));
                a.effects.get(team).forEach(e -> bukkit.addPotionEffect(e, true));
            });
        } else if (requestedTeam != null) bukkit.setDisplayName(requestedTeam.prefix + bukkit.getName());
        ((CraftPlayer) bukkit).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.GOLD + (actionbar == null ? "" : actionbar.replaceAll("\"", "\\\\\"")) + "\"}"), (byte) 2));
    }

    /**
     * Get OMGPlayer from register link.
     *
     * @param p Bukkit player.
     * @return Linked OMGPlayer, null if no OMGPlayer was linked.
     */
    public static OMGPlayer get(Player p) {
        return link.get(p);
    }

    /**
     * Reset all OMGPI data and call reset().
     */
    public void fullReset() {
        bukkit.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        displayObjective = bukkit.getScoreboard().registerNewObjective(bukkit.getName(), "dummy");
        displayObjective.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + OMGPI.g.name);
        if (OMGPI.g.state == GameState.PRELOBBY) OMGPI.g.voteSystem.scoreboardVotes();
        kit = OMGPI.g.player_latestKit(this);
        gameCoins = 0;
        played = false;
        team = OMGPI.g.spectatorTeam;
        selectedHBESlot = -1;
        actionbar = ChatColor.AQUA + "Right click any block to open \"/options\" menu.";
        hashdata.clear();
        reset();
    }

    /**
     * Reset any player stats and such.
     */
    @SuppressWarnings("deprecation")
    public void reset() {
        ((CraftPlayer) bukkit).getHandle().abilities = new PlayerAbilities();
        ((CraftPlayer) bukkit).getHandle().updateAbilities();
        for (int flag = 0; flag < 7; flag++) ((CraftPlayer) bukkit).getHandle().setFlag(flag, false);
        bukkit.setExp(0);
        bukkit.setLevel(0);
        bukkit.setMaxHealth(20);
        bukkit.setHealth(bukkit.getMaxHealth());
        bukkit.setHealthScale(20);
        bukkit.setHealthScaled(false);
        bukkit.setFoodLevel(20);
        bukkit.setFallDistance(0);
        bukkit.setSaturation(5f);
        bukkit.getActivePotionEffects().forEach(a -> bukkit.removePotionEffect(a.getType()));
        invulnerable = true;
        new BukkitRunnable() {
            public void run() {
                invulnerable = false;
            }
        }.runTaskLater(OMGPI.instance, 20L);
    }

    /**
     * Reset, teleport to module's spawn and give kit to player.
     */
    @SuppressWarnings("deprecation")
    public void respawn() {
        reset();
        bukkit.teleport(OMGPI.g.player_spawnLocation(this));
        bukkit.getWorld().playSound(bukkit.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
        bukkit.getWorld().playEffect(bukkit.getLocation(), Effect.PORTAL, 0);
        OMGPI.g.player_giveKit(this);
    }

    /**
     * Teleport player to proper world.
     */
    public void selectWorld() {
        if (OMGPI.g.checkStates(GameState.INGAME, GameState.ENDING, GameState.DISCOVERY)) {
            bukkit.teleport(OMGPI.gameworld.bukkit.getSpawnLocation());
            reset();
            if (OMGPI.g.state == GameState.DISCOVERY) {
                bukkit.setGameMode(GameMode.SPECTATOR);
                bukkit.sendMessage(ChatColor.AQUA + "Right now Discovery mode is on. You will join game on its end.");
            }
        } else {
            bukkit.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            bukkit.setGameMode(GameMode.ADVENTURE);
            reset();
            bukkit.getInventory().clear();
            OMGPI.g.event_player_joinPrelobby(this);
        }
    }

    /**
     * Play blood particles at player's location.
     */
    @SuppressWarnings("deprecation")
    public void play_particle_blood() {
        bukkit.getWorld().playEffect(bukkit.getLocation().add(0, 1, 0), Effect.TILE_BREAK, 152, Short.MAX_VALUE);
    }

    /**
     * Play effect of player getting damaged for all players.
     */
    public void play_damageEffect() {
        for (Player p1 : Bukkit.getOnlinePlayers())
            ((CraftPlayer) p1).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityStatus(((CraftPlayer) bukkit).getHandle(), (byte) 2));
    }

    /**
     * Play arrow hit sound. Usually indicates that player killed someone.
     */
    public void play_sound_ding() {
        bukkit.playSound(bukkit.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
    }

    /**
     * Play level up sound. Usually indicates start or end of game.
     */
    public void play_sound_levelup() {
        bukkit.playSound(bukkit.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    /**
     * Play ender dragon roar sound. Usually indicates some goal achieved.
     */
    public void play_sound_roar() {
        bukkit.playSound(bukkit.getLocation(), Sound.ENTITY_ENDERDRAGON_AMBIENT, 1, 1);
    }

    /**
     * Drop all items out of player inventory where they are.
     */
    public void dropItems() {
        for (ItemStack i : bukkit.getInventory().getContents())
            if (i != null && NBTParser.getTagCompound(i).getShort("Undroppable") != 1)
                bukkit.getWorld().dropItemNaturally(bukkit.getLocation(), i);
        bukkit.closeInventory();
        bukkit.getInventory().clear();
    }

    public String toString() {
        return (team == null ? "" : team.prefix) + bukkit.getName();
    }

    /**
     * Set players team, notify the player and update teams in scoreboard.
     *
     * @param team Team to set.
     */
    public void setTeam(OMGTeam team) {
        this.team = team;
        bukkit.sendMessage(ChatColor.DARK_AQUA + (team != null ? "You are in team " + team + "." : "You were removed from your team."));
        OMGPI.g.player_updateScoreboardTeams();
        update();
    }

    /**
     * Send join message of this player.
     */
    public void broadcast_join() {
        OMGPI.g.broadcast(AQUA + bukkit.getDisplayName() + AQUA + " joined the game.");
    }

    /**
     * Send join message of this player.
     */
    public void broadcast_leave() {
        OMGPI.g.broadcast(AQUA + bukkit.getDisplayName() + AQUA + " left the game.");
    }

    /**
     * Send a map description to the player.
     */
    public void sendDescription() {
        OMGPI.g.loadedMap.mapfig.description().forEach(s -> bukkit.sendMessage(ChatColor.translateAlternateColorCodes('&', (String) s)));
    }

    /**
     * Add Game Coins to player and notify him about it.
     *
     * @param i Amount to add.
     */
    public void addGameCoins(int i) {
        if (i != 0) {
            gameCoins += i;
            bukkit.sendMessage(ChatColor.DARK_GRAY + (i > 0 ? "+" : "") + i + " Game Coins!");
        }
    }

    /**
     * Check if there is only one or less players that are not spectating.
     *
     * @return Non spectator players amount &lt; 2.
     */
    public static boolean oneLeft() {
        return OMGPI.g.spectatorTeam.unpresent().size() < 2;
    }

    /**
     * Send title to player. Uses NMS.
     *
     * @param fadein   Ticks for how long will text appear.
     * @param stay     Ticks for how long will text stay on screen.
     * @param fadeout  Ticks for how long will text disappear.
     * @param title    Text to appear on screen as a title. May be null.
     * @param subtitle Text to appear on screen as a subtitle under title. May be null.
     * @param color    Mojang color for text. Will be lowercased.
     */
    public void sendTitle(int fadein, int stay, int fadeout, String title, String subtitle, String color) {
        PlayerConnection c = ((CraftPlayer) bukkit).getHandle().playerConnection;
        c.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadein, stay, fadeout));
        if (subtitle != null)
            c.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\", \"color\": \"" + color.toLowerCase() + "\"}")));
        if (title != null)
            c.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\", \"color\": \"" + color.toLowerCase() + "\"}")));
    }

    /**
     * Set requested kit.
     *
     * @param kit Kit name.
     * @param latest Also set latest kit.
     */
    public void setKit(OMGKit kit, boolean latest) {
        this.kit = kit;
        if (latest) OMGPI.g.player_set_latestKit(this, kit);
    }

    /**
     * Open hotbar editor menu.
     */
    public void hotbarEdit() {
        selectedHBESlot = -1;
        Inventory hbe = Bukkit.createInventory(bukkit, 9, "Hotbar Editor");
        String order = OMGPI.g.player_hotbarOrder(this);
        for (int c = 0; c < order.length(); c++) {
            int i = Integer.parseInt(order.charAt(c) + "");
            NBTParser clickable = OMGPI.g.kit_contents(this, kit).stream().filter(nbt -> nbt.c.getInt("Slot") == i).findFirst().orElse(new NBTParser("{id:barrier,Count:1,Slot:" + i + "}"));
            NBTTagCompound tag = clickable.c.getCompound("tag");
            NBTTagCompound display = tag.getCompound("display");
            display.setString("Name", i + 1 + "");
            tag.set("display", display);
            clickable.c.set("tag", tag);
            hbe.setItem(c, clickable.toItem());
        }
        bukkit.openInventory(hbe);
    }

    /**
     * Open options menu.
     */
    public void options() {
        if (OMGPI.g.state == GameState.PRELOBBY) bukkit.openInventory(Inventories.options);
    }
}
