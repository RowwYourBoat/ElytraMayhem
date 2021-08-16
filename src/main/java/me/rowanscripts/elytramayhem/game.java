package me.rowanscripts.elytramayhem;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class game {

    Player playerWhoStartedTheGame = null;
    Location borderLocation = null;

    FileConfiguration configuration = JavaPlugin.getPlugin(Main.class).getConfig();

    public boolean findPossibleBorderLocation() {
        borderLocation = null;
        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), () -> {

            Random random = new Random();
            int randomX = random.nextInt(100000 - 10000) - 10000;
            int randomZ = random.nextInt(100000 - 10000) - 10000;
            World currentWorld = playerWhoStartedTheGame.getWorld();
            Location possibleLocation = new Location(currentWorld, randomX, currentWorld.getHighestBlockYAt(randomX, randomZ), randomZ);
//configuration.getBoolean("findBiomeWithLand")
            if (true){
                if (possibleLocation.getBlock().getType() != Material.WATER)
                    borderLocation = possibleLocation;
            } else
                borderLocation = possibleLocation;

        }, 25);
        System.out.println(borderLocation);
        return borderLocation != null;
    }

    public boolean startGame(Player executor) {
        playerWhoStartedTheGame = executor;
        for(Player player : Bukkit.getOnlinePlayers())
            player.getInventory().clear();

        Bukkit.broadcastMessage(ChatColor.GRAY + "Looking for an appropriate battle location..");

        for(int i = 0 ; i < 10 ; i++){
            boolean success = findPossibleBorderLocation();
            System.out.println(success);
            if (success)
                break;
        }
        if (borderLocation == null)
            return false;

        Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully found an appropriate battle location!");
        Bukkit.broadcastMessage(ChatColor.GRAY + "Spawning loot chests..");

        for(Player player : Bukkit.getOnlinePlayers())
            player.teleport(borderLocation);

        return true;
    }

}
