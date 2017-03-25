package tk.omgpi.game;

import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tk.omgpi.OMGPI;
import tk.omgpi.files.OMGKit;
import tk.omgpi.utils.NBTParser;

import java.util.Collections;
import java.util.List;

/**
 * Class storing menus, ChatColors to wool ids.
 */
public class Inventories {
    public static Inventory options;
    public static Inventory teams;
    public static Inventory kits;
    public static Inventory gameShop;
    
    /**
     * Prepare all inventories.
     */
    public static void update() {
        options = Bukkit.createInventory(null, OMGPI.g.voteSystem.votes.keySet().size() > 1 ? 27 : 9, "Options");
        teams = Bukkit.createInventory(null, OMGTeam.registeredTeams.size() / 9 + 9, "Teams");
        kits = Bukkit.createInventory(null, OMGKit.kits.size() / 9 + 9, "Kits");
        gameShop = Bukkit.createInventory(null, OMGPI.g.gamefig.getInt("gameShopSize", 54), "Shop");
        
        int s = (OMGPI.g.settings.allowKits ? 1 : 0) + (OMGPI.g.settings.allowHotbarEdit ? 1 : 0);
        {
            ItemStack is = new ItemStack(Material.DIAMOND_HELMET, 1);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.BLUE + "Teams");
            im.setLore(Collections.singletonList(ChatColor.BLUE + "You can select teams or be spectator here."));
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            is.setItemMeta(im);
            options.setItem(s == 1 ? 2 : (s > 1 ? 1 : 4), is);
        }
        if (OMGPI.g.settings.allowKits) {
            ItemStack is = new ItemStack(Material.DIAMOND_SWORD, 1);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.BLUE + "Kits");
            im.setLore(Collections.singletonList(ChatColor.BLUE + "You can select kit here."));
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            is.setItemMeta(im);
            options.setItem(s == 1 ? 6 : (s == 3 ? 3 : 4), is);
        }
        if (OMGPI.g.settings.allowHotbarEdit) {
            ItemStack is = new ItemStack(Material.BLAZE_ROD, 1);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.BLUE + "Hotbar Editor");
            im.setLore(Collections.singletonList(ChatColor.BLUE + "You can select at which slots will items from kit appear in."));
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            is.setItemMeta(im);
            options.setItem(s == 1 ? 6 : (s == 3 ? 5 : (OMGPI.g.settings.allowKits ? 7 : 4)), is);
        }/* 4TH ITEM IN THE /OPTIONS
        if (somebooleanfor4thitem) {
            ItemStack is = new ItemStack(Material.STONE, 1);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.BLUE + "Name");
            im.setLore(Collections.singletonList(ChatColor.BLUE + "Desc"));
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            is.setItemMeta(im);
            options.setItem(s == 1 ? 6 : 7, is);
        }*/

        for (int i = 0; i < OMGTeam.registeredTeams.size(); i++) {
            OMGTeam t = OMGTeam.registeredTeams.get(i);
            teams.setItem(i, new NBTParser("{id:wool,Count:1,Damage:" + woolOf(ChatColor.getByChar(t.prefix.charAt(1))) + ",tag:{teamid:" + t + ",display:{Name:\"" + t.displayName + "\"}}}").toItem());
        }
        
        OMGKit.kits.forEach(kit -> {
            NBTParser nbt = new NBTParser(kit.getString("displayItem"));
            NBTTagCompound nn = nbt.c.getCompound("tag");
            nn.setString("kitid", kit.getName());
            nbt.c.set("tag", nn);
            kits.addItem(nbt.toItem());
        });

        if (OMGPI.g.voteSystem.votes.keySet().size() > 1) OMGPI.g.voteSystem.votes.keySet().forEach(m -> options.setItem(20 + OMGPI.g.voteSystem.votes.keySet().lastIndexOf(m), new ItemStack(Material.PAPER, OMGPI.g.voteSystem.votes.keySet().lastIndexOf(m)) {{
            ItemMeta im = getItemMeta();
            im.setDisplayName(ChatColor.WHITE + "Vote for " + m);
            setItemMeta(im);
        }}));
    }
    
    /**
     * Update game shop before opening.
     */
    public static void gameShopUpdate() {
        if (OMGPI.g.loadedMap != null) {
            gameShop.clear();
            List<String> nbts = OMGPI.g.gamefig.getStringList("gameShop");
            for (int i = 0; i < nbts.size(); i++) {
                NBTParser item = new NBTParser(nbts.get(i));
                if (item.toItem() != null) gameShop.setItem(i, item.toItem());
            }
        }
    }
    
    /**
     * Get wool data value from given ChatColor.
     * Note that RED is pink, DARK_RED is red and BLACK and DARK_BLUE are same.
     *
     * @param c ChatColor that needs converting.
     * @return the data value.
     */
    public static short woolOf(ChatColor c) {
        switch (c) {
            case BLACK:
            case DARK_BLUE:
                return 15;
            case DARK_GREEN:
                return 13;
            case DARK_AQUA:
                return 9;
            case DARK_RED:
                return 14;
            case DARK_PURPLE:
                return 10;
            case GOLD:
                return 1;
            case GRAY:
                return 8;
            case DARK_GRAY:
                return 7;
            case BLUE:
                return 11;
            case GREEN:
                return 5;
            case AQUA:
                return 3;
            case RED:
                return 6;
            case LIGHT_PURPLE:
                return 2;
            case YELLOW:
                return 4;
            case WHITE:
                return 0;
        }
        throw new RuntimeException("New color?!");
    }
    
    public static void openFakeInv(Inventory i, OMGPlayer p) {
        Inventory fake = Bukkit.createInventory(p.bukkit, i.getSize(), i.getTitle());
        for (int slot = 0; slot < i.getContents().length; slot++) {
            ItemStack e = i.getContents()[slot];
            if (e != null) {
                e = e.clone();
                ItemMeta im = e.getItemMeta();
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                if ((NBTParser.getTagCompound(e).hasKey("kitid") && NBTParser.getTagCompound(e).getString("kitid").equals(p.kit.getName())) || (NBTParser.getTagCompound(e).hasKey("teamid") && NBTParser.getTagCompound(e).getString("teamid").equals(p.requestedTeam.id)))
                    im.addEnchant(Enchantment.DURABILITY, 1, true);
                e.setItemMeta(im);
                fake.setItem(slot, e);
            }
        }
        p.bukkit.openInventory(fake);
    }
}
