package me.rowanscripts.elytramayhem;

import me.rowanscripts.elytramayhem.getMethods.defaultLootItems;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class settings {

    JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);

    public boolean settingsManager(Player executor, String[] args){

        File settingsFile = new File(plugin.getDataFolder(), "settings.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(settingsFile);
        File lootFile = new File(plugin.getDataFolder(), "loot.yml");
        FileConfiguration lootData = YamlConfiguration.loadConfiguration(lootFile);

        if (args.length < 2)
            executor.sendMessage("/battle settings <setting|reset> <set|get> <value>");
        else if (args[1].equalsIgnoreCase("reset"))
            try {
                settingsData.set("findBiomeWithLand", true); // forces the plugin to find a biome with at least some land
                settingsData.set("playersGlow", true); // toggle whether players will glow during rounds
                settingsData.set("specialOccurrences", true); // toggles random weather and time events, like thunder & nighttime.
                settingsData.set("amountOfFireworksAtStart", 3); // how many fireworks each player will receive at the start (limit: 64)
                settingsData.set("borderSize", 150); // the size of the border (minimum: 100, limit: 500)
                settingsData.set("maxItemsInOneChest", 5); // the maximum amount of items in one chest (limit: 27)
                settingsData.set("amountOfChests", 10); // the amount of loot chests that will spawn (limit: 50)
                settingsData.createSection("battleRoyaleMode"); // section
                settingsData.set("battleRoyaleMode.enabled", false); // toggles battle royale mode, where the border shrinks
                settingsData.set("battleRoyaleMode.borderShrinkingDurationInSeconds", 300); // how long it takes for the border to shrink all the way
                settingsData.options().header("Visit the following website for information:\nhttps://github.com/icallhacks/ElytraMayhem/blob/master/README.md");
                lootData.set("Enchantments", true);
                lootData.options().header("There is a 20% chance that an item will be enchanted when Enchantments is true.\n You can add a loot item by copying a different item and editing the value(s). If you mess up & the plugin breaks, use /battle settings reset.");
                defaultLootItems defaultLootItems = new defaultLootItems();
                List<ItemStack> lootItemsList = defaultLootItems.getDefaultLootItems();
                lootData.set("lootItems", lootItemsList);
                settingsData.save(settingsFile);
                lootData.save(lootFile);
                executor.sendMessage(ChatColor.RED + "You've successfully reset all configuration files!");
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
                    } else if (!isValueWithinBounds(setting, Integer.parseInt(value))) {
                        executor.sendMessage(ChatColor.RED + "Input out of bounds!");
                        return true;
                    }

                    settingsData.set(setting, Integer.parseInt(value));
                }

                try {
                    settingsData.save(settingsFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                executor.sendMessage("Changed the value of " + setting + " to: " + value);

            }

        }

        return true;
    }

    public boolean isValueWithinBounds(String setting, Integer value) {

        if (setting.equals("borderSize") && (value < 100 || value > 500))
            return false;
        else if (setting.equals("amountOfFireworksAtStart") && (value > 64))
            return false;
        else if (setting.equals("maxItemsInOneChest") && (value > 27))
            return false;
        else if (setting.equals("amountOfChests") && (value > 50))
            return false;
        else return value > 0;

    }

}
