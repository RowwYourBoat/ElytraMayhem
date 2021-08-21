package me.rowanscripts.elytramayhem.getMethods;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class defaultLootItems {

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
        lootItems.add(new ItemStack(Material.COOKED_PORKCHOP, 8));
        lootItems.add(new ItemStack(Material.GOLDEN_APPLE));

        lootItems.add(new ItemStack(Material.ENDER_PEARL));
        lootItems.add(new ItemStack(Material.FIREWORK_ROCKET));
        lootItems.add(new ItemStack(Material.FIREWORK_ROCKET));
        lootItems.add(new ItemStack(Material.FIREWORK_ROCKET));

        return lootItems;
    }

}
