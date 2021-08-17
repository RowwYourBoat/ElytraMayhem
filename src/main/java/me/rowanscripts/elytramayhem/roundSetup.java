package me.rowanscripts.elytramayhem;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
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

        WorldBorder border = currentWorld.getWorldBorder();
        border.setCenter(borderLocation);
        border.setSize(configuration.getDouble("borderSize"));

        Location aboveArenaTeleportLocation = new Location(currentWorld, borderLocation.getX(), borderLocation.getY() + 50, borderLocation.getZ(), 0, 90);

        for(Player player : Bukkit.getOnlinePlayers())
            player.teleport(aboveArenaTeleportLocation);

    }

    public void fillLootChest(Material chest){

    }

    public void spawnLootChests(Player playerWhoStartedTheGame) {

        int amountOfChests = configuration.getInt("amountOfChests");
        int borderSize = configuration.getInt("borderSize");

        Random random = new Random();
        World currentWorld = playerWhoStartedTheGame.getWorld();
        for(int i = 0 ; i < (amountOfChests + 1) ; i++){
            int randomX = random.nextInt(((int) borderLocation.getX()+(borderSize/2)) - ((int) borderLocation.getX()-(borderSize/2))) + ((int) borderLocation.getX()+(borderSize/2));
            int randomY = random.nextInt(200 - 125) + 125;
            int randomZ = random.nextInt(((int) borderLocation.getZ()+(borderSize/2)) - ((int) borderLocation.getZ()-(borderSize/2))) + ((int) borderLocation.getZ()+(borderSize/2));
            Location chestLocation = new Location(currentWorld, randomX, randomY, randomZ);
            System.out.println(chestLocation);
            Location blockUnderChestLocation = new Location(currentWorld, randomX, randomY - 1, randomZ);
            chestLocation.getBlock().setType(Material.CHEST);
            blockUnderChestLocation.getBlock().setType(Material.SEA_LANTERN);

            fillLootChest(currentWorld.getBlockAt(chestLocation).getType());
            for(Player player : Bukkit.getOnlinePlayers())
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Generating Loot Chests: " + i + "/" + amountOfChests));
        }

    }

}