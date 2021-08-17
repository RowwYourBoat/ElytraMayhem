package me.rowanscripts.elytramayhem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class roundSetup {

    FileConfiguration configuration = JavaPlugin.getPlugin(Main.class).getConfig();

    Location borderLocation = null;

    public void findPossibleBorderLocation(Player playerWhoStartedTheGame) {

        Random random = new Random();
        int randomX = random.nextInt(100000 - 10000) - 10000;
        int randomZ = random.nextInt(100000 - 10000) - 10000;
        World currentWorld = playerWhoStartedTheGame.getWorld();
        Location possibleLocation = new Location(currentWorld, randomX, currentWorld.getHighestBlockYAt(randomX, randomZ), randomZ);

        if (configuration.getBoolean("findBiomeWithLand")){
            if (possibleLocation.getBlock().getType() != Material.WATER)
                borderLocation = possibleLocation;
            else
                findPossibleBorderLocation(playerWhoStartedTheGame);
        } else
            borderLocation = possibleLocation;

    }

    public void teleportPlayersAboveLocation(Player playerWhoStartedTheGame) {

        World currentWorld = playerWhoStartedTheGame.getWorld();
        Location aboveArenaTeleportLocation = new Location(currentWorld, borderLocation.getX(), borderLocation.getY() + 50, borderLocation.getZ(), 0, 90);

        for(Player player : Bukkit.getOnlinePlayers())
            player.teleport(aboveArenaTeleportLocation);

    }

}