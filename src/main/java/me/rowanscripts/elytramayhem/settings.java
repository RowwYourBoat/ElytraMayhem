package me.rowanscripts.elytramayhem;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class settings {

    JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);

    public boolean settingsManager(Player executor, String[] args){

        File f = new File(plugin.getDataFolder(), "settings.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(f);

        if (args.length < 2)
            executor.sendMessage("/battle settings <list|setting|reset> <set|get> <value>");
        else if (args[1].equalsIgnoreCase("list"))
            executor.sendMessage("findBiomeWithLand, playersGlow, battleRoyaleMode, amountOfFireworksAtStart, borderSize, maxItemsInOneChest, amountOfChests");
        else if (args[1].equalsIgnoreCase("reset"))
            try {
                settingsData.set("findBiomeWithLand", true); // forces the plugin to find a biome with at least some land
                settingsData.set("playersGlow", true); // toggle whether players will glow during rounds
                settingsData.set("battleRoyaleMode", false); // toggles battle royale mode, where the border shrinks
                settingsData.set("amountOfFireworksAtStart", 3); // how many fireworks each player will receive at the start (limit: 64)
                settingsData.set("borderSize", 150); // the size of the border (minimum: 100, limit: 500)
                settingsData.set("maxItemsInOneChest", 5); // the maximum amount of items in one chest (limit: 27)
                settingsData.set("amountOfChests", 10); // the amount of loot chests that will spawn (limit: 50)
                List<ItemStack> lootItemsList = getDefaultLootItems();
                settingsData.set("lootItems", lootItemsList);
                settingsData.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        else if (settingsData.contains(args[1])){
            if (args.length < 3) {
                executor.sendMessage("/battle settings <list|setting> <set|get> <value>");
                return true;
            }
            String setting = args[1];
            String resultType = args[2];
            if (resultType.equalsIgnoreCase("get"))
                executor.sendMessage("The current value of " + setting + " is: " + settingsData.get(setting).toString());
            else if (resultType.equalsIgnoreCase("set")){
                if (args.length < 4){
                    executor.sendMessage("/battle settings <list|setting> <set|get> <value>");
                    return true;
                }

                String value = args[3];

                if (settingsData.isBoolean(setting)) {
                    if (value.equals("true"))
                        settingsData.set(setting, true);
                    else if (value.equals("false"))
                        settingsData.set(setting, false);
                    else {
                        executor.sendMessage(ChatColor.RED + "Invalid input!");
                        return true;
                    }
                } else if (settingsData.isInt(setting)) {
                    if (value.equals("false") || value.equals("true")) {
                        executor.sendMessage(ChatColor.RED + "Invalid input!");
                        return true;
                    }
                    settingsData.set(setting, Integer.parseInt(value));
                }

                try {
                    settingsData.save(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                executor.sendMessage("Changed the value of " + setting + " to: " + value);

            }

        }

        return true;
    }

    public List<ItemStack> getDefaultLootItems(){
        List<ItemStack> lootItems = new ArrayList<>();

        lootItems.add(new ItemStack(Material.WOODEN_SWORD));
        lootItems.add(createItemStackWithEnchantment(Material.WOODEN_SWORD, Enchantment.DAMAGE_ALL, 2));
        lootItems.add(new ItemStack(Material.STONE_SWORD));
        lootItems.add(createItemStackWithEnchantment(Material.STONE_SWORD, Enchantment.DAMAGE_ALL, 2));
        lootItems.add(new ItemStack(Material.IRON_SWORD));
        lootItems.add(createItemStackWithEnchantment(Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 2));

        lootItems.add(new ItemStack(Material.WOODEN_AXE, 1));
        lootItems.add(new ItemStack(Material.STONE_AXE, 1));

        lootItems.add(new ItemStack(Material.BOW));
        lootItems.add(new ItemStack(Material.ARROW, 5));
        lootItems.add(new ItemStack(Material.ARROW, 10));

        lootItems.add(new ItemStack(Material.LEATHER_BOOTS));
        lootItems.add(createItemStackWithEnchantment(Material.LEATHER_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        lootItems.add(new ItemStack(Material.CHAINMAIL_BOOTS));
        lootItems.add(new ItemStack(Material.IRON_BOOTS));
        lootItems.add(createItemStackWithEnchantment(Material.IRON_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        lootItems.add(new ItemStack(Material.LEATHER_LEGGINGS));
        lootItems.add(createItemStackWithEnchantment(Material.LEATHER_LEGGINGS, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        lootItems.add(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        lootItems.add(createItemStackWithEnchantment(Material.CHAINMAIL_LEGGINGS, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        lootItems.add(new ItemStack(Material.IRON_LEGGINGS));
        lootItems.add(new ItemStack(Material.LEATHER_CHESTPLATE));
        lootItems.add(createItemStackWithEnchantment(Material.LEATHER_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        lootItems.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        lootItems.add(createItemStackWithEnchantment(Material.CHAINMAIL_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        lootItems.add(new ItemStack(Material.IRON_CHESTPLATE));
        lootItems.add(new ItemStack(Material.LEATHER_HELMET));
        lootItems.add(createItemStackWithEnchantment(Material.LEATHER_HELMET, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        lootItems.add(new ItemStack(Material.CHAINMAIL_HELMET));
        lootItems.add(new ItemStack(Material.IRON_HELMET));
        lootItems.add(createItemStackWithEnchantment(Material.IRON_HELMET, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
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

    public ItemStack createItemStackWithEnchantment(Material item, Enchantment enchantment, Integer level){
        ItemStack enchantedItem = new ItemStack(item);
        enchantedItem.addEnchantment(enchantment, level);

        return enchantedItem;
    }
}
