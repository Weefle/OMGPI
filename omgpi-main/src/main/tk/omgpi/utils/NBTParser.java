package tk.omgpi.utils;

import net.minecraft.server.v1_11_R1.MojangsonParseException;
import net.minecraft.server.v1_11_R1.MojangsonParser;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTParser {
    public net.minecraft.server.v1_11_R1.NBTTagCompound c;

    public NBTParser(String s) {
        this.c = parseCompound(s);
    }

    /**
     * Get item from stored compound.
     * @return Bukkit ItemStack, null if no compound is stored.
     */
    public org.bukkit.inventory.ItemStack toItem() {
        return c != null ? CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_11_R1.ItemStack(c)) : null;
    }

    /**
     * Parse compound out of string.
     * @param s NBT string.
     * @return Parsed NBT tag.
     */
    public static NBTTagCompound parseCompound(String s) {
        try {
            return MojangsonParser.parse(ChatColor.translateAlternateColorCodes('&', s));
        } catch (MojangsonParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get NBT tag compound of already existing ItemStack.
     * @param i Bukkit ItemStack.
     * @return Parsed NBT tag.
     */
    public static net.minecraft.server.v1_11_R1.NBTTagCompound getTagCompound(ItemStack i) {
        return CraftItemStack.asNMSCopy(i).getTag() != null ? CraftItemStack.asNMSCopy(i).getTag() : new net.minecraft.server.v1_11_R1.NBTTagCompound();
    }

    /**
     * Set NBT tag compound of ItemStack clone.
     * @param i Bukkit ItemStack.
     * @param nbt Parsed NBT tag.
     * @return ItemStack with changed tag.
     */
    public static ItemStack setTagCompound(org.bukkit.inventory.ItemStack i, net.minecraft.server.v1_11_R1.NBTTagCompound nbt) {
        net.minecraft.server.v1_11_R1.ItemStack r = CraftItemStack.asNMSCopy(i);
        r.setTag(nbt);
        return CraftItemStack.asBukkitCopy(r);
    }
}
