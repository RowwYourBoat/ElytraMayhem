package me.rowanscripts.elytramayhem.getMethods;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class defaultLootItems {

    public ItemStack newFirework(ItemStack itemStack, String name, int power, FireworkEffect fireworkEffect){
        FireworkMeta meta = (FireworkMeta) itemStack.getItemMeta();
        meta.setDisplayName(name);
        meta.setPower(power);
        meta.addEffect(fireworkEffect);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public List<ItemStack> getDefaultLootItems() {
        List<ItemStack> lootItems = new ArrayList<>();

        lootItems.add(new ItemStack(Material.WOODEN_SWORD));
        lootItems.add(new ItemStack(Material.STONE_SWORD));
        lootItems.add(new ItemStack(Material.IRON_SWORD));

        lootItems.add(new ItemStack(Material.WOODEN_AXE, 1));
        lootItems.add(new ItemStack(Material.STONE_AXE, 1));

        lootItems.add(new ItemStack(Material.BOW));
        lootItems.add(new ItemStack(Material.CROSSBOW));
        lootItems.add(new ItemStack(Material.ARROW, 5));
        lootItems.add(new ItemStack(Material.ARROW, 10));

        lootItems.add(new ItemStack(Material.LEATHER_BOOTS));
        lootItems.add(new ItemStack(Material.CHAINMAIL_BOOTS));
        lootItems.add(new ItemStack(Material.IRON_BOOTS));
        lootItems.add(new ItemStack(Material.LEATHER_LEGGINGS));
        lootItems.add(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        lootItems.add(new ItemStack(Material.IRON_LEGGINGS));
        lootItems.add(new ItemStack(Material.LEATHER_CHESTPLATE));
        lootItems.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        lootItems.add(new ItemStack(Material.IRON_CHESTPLATE));
        lootItems.add(new ItemStack(Material.LEATHER_HELMET));
        lootItems.add(new ItemStack(Material.CHAINMAIL_HELMET));
        lootItems.add(new ItemStack(Material.IRON_HELMET));
        lootItems.add(new ItemStack(Material.SHIELD));

        lootItems.add(new ItemStack(Material.COOKED_BEEF, 8));
        lootItems.add(new ItemStack(Material.BREAD, 16));
        lootItems.add(new ItemStack(Material.BREAD, 16));
        lootItems.add(new ItemStack(Material.BREAD, 16));
        lootItems.add(new ItemStack(Material.COOKED_PORKCHOP, 8));
        lootItems.add(new ItemStack(Material.GOLDEN_APPLE));

        lootItems.add(new ItemStack(Material.ENDER_PEARL));
        lootItems.add(new ItemStack(Material.FIREWORK_ROCKET));
        lootItems.add(new ItemStack(Material.FIREWORK_ROCKET));
        lootItems.add(newFirework(new ItemStack(Material.FIREWORK_ROCKET), ChatColor.RED + "Crossbow Ammo", 2, FireworkEffect.builder().flicker(true).trail(true).withColor(Color.RED, Color.WHITE, Color.BLUE).build()));
        lootItems.add(newFirework(new ItemStack(Material.FIREWORK_ROCKET), ChatColor.RED + "Crossbow Ammo", 2, FireworkEffect.builder().flicker(true).trail(true).withColor(Color.RED, Color.WHITE, Color.BLUE).build()));
        lootItems.add(newFirework(new ItemStack(Material.FIREWORK_ROCKET), ChatColor.GOLD + "SUPER FIREWORK", 5, FireworkEffect.builder().flicker(true).trail(true).withColor(Color.YELLOW).build()));
        lootItems.add(new ItemStack(Material.FIREWORK_ROCKET));

        return lootItems;
    }

}
