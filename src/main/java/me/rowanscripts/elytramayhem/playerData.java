package me.rowanscripts.elytramayhem;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class playerData {

    JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);

    public void createDefaultFileIfNonExists(Player player) {
        File folder = new File(plugin.getDataFolder(), "playerData");
        File dataFile = new File(folder, File.separator + player.getUniqueId() + ".yml");

        if (dataFile.exists()) // if data file already exists
            return;

        try {
            YamlConfiguration playerData = YamlConfiguration.loadConfiguration(dataFile);
            playerData.options().header(player.getName());
            playerData.set("Wins", 0);
            playerData.set("Kills", 0);
            playerData.set("Deaths", 0);
            playerData.set("Rounds", 0);
            playerData.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(Player player, String dataType) {
        createDefaultFileIfNonExists(player);

        File folder = new File(plugin.getDataFolder(), "playerData");
        File dataFile = new File(folder, File.separator + player.getUniqueId() + ".yml");
        YamlConfiguration playerData = YamlConfiguration.loadConfiguration(dataFile);

        int currentValue = playerData.getInt(dataType);
        playerData.set(dataType, (currentValue + 1));

        try {
            playerData.options().header(player.getName());
            playerData.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer get(Player player, String dataType) {
        createDefaultFileIfNonExists(player);

        File folder = new File(plugin.getDataFolder(), "playerData");
        File dataFile = new File(folder, File.separator + player.getUniqueId() + ".yml");
        YamlConfiguration playerData = YamlConfiguration.loadConfiguration(dataFile);

        return playerData.getInt(dataType);

    }

}