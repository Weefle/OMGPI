package tk.omgpi.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tk.omgpi.events.player.OMGBreakEvent;
import tk.omgpi.events.player.OMGDamageCause;
import tk.omgpi.events.player.OMGDamageEvent;
import tk.omgpi.events.player.OMGPlaceEvent;
import tk.omgpi.files.OMGKit;
import tk.omgpi.files.OMGLoot;
import tk.omgpi.game.*;
import tk.omgpi.utils.NBTParser;
import tk.omgpi.utils.ReflectionUtils;

import java.util.List;

import static tk.omgpi.OMGPI.g;
import static tk.omgpi.game.OMGPlayer.get;
import static tk.omgpi.utils.ReflectionUtils.cbclasses;
import static tk.omgpi.utils.ReflectionUtils.nmsclasses;

/**
 * Bukkit event listener. Has no JavaDocs inside, but shows which events are handled
 */
public class BukkitEventHandler implements Listener {
    @EventHandler
    public void event(PlayerJoinEvent e) {
        g.event_player_join(new OMGPlayer(e.getPlayer()));
    }

    @EventHandler
    public void event(PlayerQuitEvent e) {
        OMGPlayer p = get(e.getPlayer());
        if (p == null) return;
        p.remove();
        g.event_player_leave(p);
    }

    @EventHandler
    public void event(PlayerDropItemEvent e) {
        if (NBTParser.getTagCompound(e.getItemDrop().getItemStack()).getShort("Undroppable") == 1) e.setCancelled(true);
    }

    @EventHandler
    public void event(ProjectileLaunchEvent e) {
        g.shootSources.put(e.getEntity(), e.getEntity().getLocation());
    }

    @EventHandler
    public void event(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            OMGPlayer damaged = get((Player) e.getEntity());
            if (g.state != GameState.INGAME || damaged.team == g.spectatorTeam || damaged.invulnerable || (Area.registeredAreas.values().stream().anyMatch(a -> a.isInside(e.getEntity().getLocation().getBlock().getLocation()) && (!a.cancelDamage.containsKey(damaged.team) || a.cancelDamage.get(damaged.team).contains(OMGDamageCause.getByBukkit(e.getCause())))))) {
                e.setCancelled(true);
                return;
            }
            if (e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                if (e.getCause() == EntityDamageEvent.DamageCause.VOID) e.setDamage(200);
                new OMGDamageEvent(e, get((Player) e.getEntity()), null, OMGDamageCause.getByBukkit(e.getCause()), (float) e.getDamage());
            }
        }
    }

    @EventHandler
    public void event(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            OMGPlayer damaged = get((Player) e.getEntity());
            if (g.state != GameState.INGAME || damaged.team == g.spectatorTeam || damaged.invulnerable || (Area.registeredAreas.values().stream().anyMatch(a -> a.isInside(e.getEntity().getLocation().getBlock().getLocation()) && (!a.cancelDamage.containsKey(damaged.team) || a.cancelDamage.get(damaged.team).contains(OMGDamageCause.getByBukkit(e.getCause())))))) {
                e.setCancelled(true);
                return;
            }
            if (e.getDamager() instanceof Projectile) damaged.lastProjectileShotBy = (Projectile) e.getDamager();
            if (e.getDamager() instanceof Player || (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)) {
                OMGPlayer damager = e.getDamager() instanceof Player ? get((Player) e.getDamager()) : get((Player) ((Projectile) e.getDamager()).getShooter());
                if (damaged.team == damager.team && !damaged.team.allowFriendlyFire) {
                    e.setCancelled(true);
                    e.setDamage(0);
                    return;
                }
                damaged.lastDamager = damager;
            }
            new OMGDamageEvent(e, get((Player) e.getEntity()), e.getDamager(), OMGDamageCause.getByBukkit(e.getCause()), (float) e.getDamage());
        }
    }

    @EventHandler
    public void event(PlayerMoveEvent e) {
        if (!GameArea.isInside(e.getTo())) {
            Location old = e.getFrom().getBlock().getLocation().add(0.5, 0, 0.5);
            old.setDirection(e.getFrom().getDirection());
            e.getPlayer().teleport(old);
            e.getPlayer().sendMessage(ChatColor.RED + "You can't go so far!");
        }
    }

    @EventHandler
    public void event(FoodLevelChangeEvent e) {
        if (g.state != GameState.INGAME) e.setFoodLevel(20);
    }

    @EventHandler
    public void event(BlockPlaceEvent e) {
        new OMGPlaceEvent(e, get(e.getPlayer()), e.getBlock());
        if (Area.registeredAreas.values().stream().anyMatch(a -> a.isInside(e.getBlock().getLocation())) && Area.registeredAreas.values().stream().noneMatch(a -> a.isInside(e.getBlock().getLocation()) && a.isPlaceAllowed(get(e.getPlayer()).team, e.getBlock().getType()))) {
            e.setCancelled(true);
            return;
        }
        if (!GameArea.isBlockInside(e.getBlock())) e.setCancelled(true);
    }

    @EventHandler
    public void event(BlockBreakEvent e) {
        new OMGBreakEvent(e, get(e.getPlayer()), e.getBlock());
        if (Area.registeredAreas.values().stream().anyMatch(a -> a.isInside(e.getBlock().getLocation())) && Area.registeredAreas.values().stream().noneMatch(a -> a.isInside(e.getBlock().getLocation()) && a.isBreakAllowed(get(e.getPlayer()).team, e.getBlock().getType()))) {
            e.setCancelled(true);
            return;
        }
        if (g.settings.isLootingOn && e.getBlock().getType() == Material.CHEST) {
            e.setCancelled(true);
            Inventory inv = ((Chest) e.getBlock().getState()).getBlockInventory();
            String lootid = inv.getTitle() == null ? "" : inv.getTitle();
            OMGLoot.LootParser lp = g.loot_contents(get(e.getPlayer()), lootid);
            if (lp != null) {
                inv.clear();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack ii = lp.getRandom().toItem();
                    if (ii != null && NBTParser.getTagCompound(ii).getByte("Undroppable") != 1)
                        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), ii);
                }
            }
            e.getBlock().setType(Material.AIR);
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.CHEST));
        }
        if (!GameArea.isBlockInside(e.getBlock())) e.setCancelled(true);
    }

    @EventHandler
    public void event(BlockBurnEvent e) {
        if (g.state != GameState.INGAME) {
            e.setCancelled(true);
            return;
        }
        if (Area.registeredAreas.values().stream().filter(a -> a.isInside(e.getBlock().getLocation())).noneMatch(a -> a.allowBurn)) {
            e.setCancelled(true);
            return;
        }
        if (g.settings.isLootingOn && e.getBlock().getType() == Material.CHEST) {
            e.setCancelled(true);
            Inventory inv = ((Chest) e.getBlock().getState()).getBlockInventory();
            String lootid = inv.getTitle() == null ? "" : inv.getTitle();
            OMGLoot.LootParser lp = g.loot_contents(null, lootid);
            if (lp != null) {
                inv.clear();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack ii = lp.getRandom().toItem();
                    if (ii != null && NBTParser.getTagCompound(ii).getByte("Undroppable") != 1)
                        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), ii);
                }
            }
            e.getBlock().setType(Material.AIR);
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.CHEST));
        }
        if (!GameArea.isBlockInside(e.getBlock())) e.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerInteractEvent e) {
        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) && (e.getItem() == null || e.getItem().getType() == Material.AIR)) {
            if (g.checkStates(GameState.INGAME, GameState.ENDING)) {
                if (Area.registeredAreas.values().stream().anyMatch(a -> a.isInside(e.getPlayer().getLocation().getBlock().getLocation()) && a.isShop(get(e.getPlayer()).team))) {
                    g.player_openGameShop(get(e.getPlayer()));
                    e.setCancelled(true);
                }
            } else {
                get(e.getPlayer()).options();
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void event(BlockFromToEvent e) {
        if (Area.registeredAreas.values().stream().anyMatch(a -> a.isInside(e.getToBlock().getLocation())) && Area.registeredAreas.values().stream().filter(a -> a.isInside(e.getToBlock().getLocation())).noneMatch(a -> a.allowFlow))
            e.setCancelled(true);
    }

    @EventHandler
    public void event(EntityExplodeEvent e) {
        e.blockList().removeIf(b -> Area.registeredAreas.values().stream().anyMatch(a -> a.isInside(b.getLocation())) && Area.registeredAreas.values().stream().filter(a -> a.isInside(b.getLocation())).noneMatch(a -> a.canExplode.contains(b.getType())));
        if (g.state == GameState.INGAME && g.settings.isLootingOn)
            e.blockList().removeIf(b -> {
                if (b.getType() == Material.CHEST) {
                    Inventory inv = ((Chest) b.getState()).getBlockInventory();
                    String lootid = inv.getTitle() == null ? "" : inv.getTitle();
                    OMGLoot.LootParser lp = g.loot_contents(null, lootid);
                    if (lp != null) {
                        inv.clear();
                        for (int i = 0; i < inv.getSize(); i++) {
                            ItemStack ii = lp.getRandom().toItem();
                            if (ii != null && NBTParser.getTagCompound(ii).getByte("Undroppable") != 1)
                                b.getWorld().dropItemNaturally(b.getLocation(), ii);
                        }
                    }
                    b.setType(Material.AIR);
                    b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.CHEST));
                    return true;
                }
                return false;
            });
    }

    @EventHandler
    public void event(InventoryOpenEvent e) {
        if (g.state == GameState.INGAME && g.settings.isLootingOn && e.getInventory().getHolder() instanceof Chest) {
            String lootid = e.getInventory().getTitle() == null ? "" : e.getInventory().getTitle();
            if (get((Player) e.getPlayer()).team == g.spectatorTeam && OMGLoot.loots.stream().anyMatch(l -> l.getName().equals(lootid))) {
                e.setCancelled(true);
                return;
            }
            OMGLoot.LootParser lp = g.loot_contents(get((Player) e.getPlayer()), lootid);
            if (lp != null) {
                e.getInventory().clear();
                for (int i = 0; i < e.getInventory().getSize(); i++)
                    e.getInventory().setItem(i, lp.getRandom().toItem());
                e.getPlayer().closeInventory();
                Chest c = (Chest) e.getInventory().getHolder();
                //Reset custom name
                if (ReflectionUtils.intVer() < 11) {
                    try {
                        Class<?> craftBlockState = ReflectionUtils.getClazz(cbclasses, "CraftBlockState");
                        Class<?> tileEntityChest = ReflectionUtils.getClazz(nmsclasses, "TileEntityChest");
                        tileEntityChest.getDeclaredMethod("a", String.class).invoke(craftBlockState.getDeclaredMethod("getTileEntity").invoke(c), (String) null);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    try {
                        Class<?> craftLootable = ReflectionUtils.getClazz(cbclasses, "CraftLootable");
                        craftLootable.getDeclaredMethod("setCustomName", String.class).invoke(c, (String) null);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                c.update();
                e.getPlayer().openInventory(c.getBlockInventory());
            }
        }
    }

    @EventHandler
    public void event(InventoryClickEvent e) {
        if (e.getCurrentItem() == null){ return; }
        if (g.state == GameState.INGAME && e.getInventory().getType() == InventoryType.ANVIL && e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName() && OMGLoot.loots.stream().anyMatch(l -> l.name.equals(e.getCurrentItem().getItemMeta().getDisplayName()))) {
            e.setCancelled(true);
            return;
        }
        if (e.getClickedInventory() != null && e.getClickedInventory().getTitle() != null) {
            if (e.getClickedInventory().getTitle().equals(Inventories.options.getTitle()) && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Teams"))
                    Inventories.openFakeInv(Inventories.teams, get((Player) e.getWhoClicked()));
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Kits"))
                    Inventories.openFakeInv(Inventories.kits, get((Player) e.getWhoClicked()));
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Hotbar"))
                    get((Player) e.getWhoClicked()).hotbarEdit();
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Vote") && g.voteSystem.vote(get((Player) e.getWhoClicked()), e.getCurrentItem().getItemMeta().getDisplayName().replaceAll(ChatColor.WHITE + "Vote for ", "")))
                    e.getWhoClicked().sendMessage(ChatColor.DARK_AQUA + "Voted for " + e.getCurrentItem().getItemMeta().getDisplayName().replaceAll(ChatColor.WHITE + "Vote for ", ""));

                e.setCancelled(true);
            } else if (e.getClickedInventory().getTitle().equals(Inventories.teams.getTitle())) {
                OMGTeam.registeredTeams.stream().filter(t -> t.id.equals(NBTParser.getTagCompound(e.getCurrentItem()).getString("teamid"))).findFirst().ifPresent(t -> g.player_request_team(get((Player) e.getWhoClicked()), t));
                Inventories.openFakeInv(Inventories.teams, get((Player) e.getWhoClicked()));
                e.setCancelled(true);
            } else if (e.getClickedInventory().getTitle().equals(Inventories.kits.getTitle())) {
                OMGKit.kits.stream().filter(k -> k.name.equals(NBTParser.getTagCompound(e.getCurrentItem()).getString("kitid"))).findFirst().ifPresent(k -> g.player_request_kit(get((Player) e.getWhoClicked()), OMGKit.kits.stream().filter(k1 -> k1 == k).findFirst().orElse(null)));
                Inventories.openFakeInv(Inventories.kits, get((Player) e.getWhoClicked()));
                e.setCancelled(true);
            } else if (e.getClickedInventory().getTitle().equals("Hotbar Editor")) {
                if (e.getCurrentItem() != null) {
                    e.setCancelled(true);
                    if (get((Player) e.getWhoClicked()).selectedHBESlot != -1) {
                        String order = g.player_hotbarOrder(get((Player) e.getWhoClicked()));
                        int b = Integer.parseInt(e.getCurrentItem().getItemMeta().getDisplayName()) - 1;
                        char[] ss = order.toCharArray();
                        ss[order.indexOf(get((Player) e.getWhoClicked()).selectedHBESlot + "")] = (b + "").charAt(0);
                        ss[order.indexOf(b + "")] = (get((Player) e.getWhoClicked()).selectedHBESlot + "").charAt(0);
                        g.player_set_hotbarOrder(get((Player) e.getWhoClicked()), new String(ss));
                        get((Player) e.getWhoClicked()).hotbarEdit();
                    } else {
                        get((Player) e.getWhoClicked()).selectedHBESlot = Integer.parseInt(e.getCurrentItem().getItemMeta().getDisplayName()) - 1;
                        e.getClickedInventory().clear(e.getSlot());
                    }
                }
                e.setCancelled(true);
            } else if (e.getClickedInventory().getTitle().equals(Inventories.gameShop.getTitle())) {
                List<String> nbts = g.gamefig.getStringList("gameShop");
                if (e.getSlot() < nbts.size()) {
                    NBTParser nbt = new NBTParser(nbts.get(e.getSlot()));
                    if (!nbt.getString("id").toLowerCase().contains("air"))
                        g.player_giveShopItem(get((Player) e.getWhoClicked()), nbt);
                }
                e.setCancelled(true);
            }
        }
    }
}
