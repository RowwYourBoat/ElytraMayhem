package me.rowanscripts.elytramayhem;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class settings {

    JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);

    public boolean settingsManager(Player executor, String[] args){

        File f = new File(plugin.getDataFolder(), "settings.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(f);

        if (args.length < 2)
            executor.sendMessage("/battle settings <list|setting> <set|get> <value>");
        else if (args[1].equalsIgnoreCase("list"))
            executor.sendMessage("findBiomeWithLand, playersGlow, battleRoyaleMode, amountOfFireworksAtStart, borderSize, maxItemsInOneChest, amountOfChests");
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
}
