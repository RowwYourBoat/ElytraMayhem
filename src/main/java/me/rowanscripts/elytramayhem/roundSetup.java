package me.rowanscripts.elytramayhem;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
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

        Location aboveArenaTeleportLocation = new Location(currentWorld, borderLocation.getX(), 300, borderLocation.getZ(), 0, 90);

        for(Player player : Bukkit.getOnlinePlayers())
            player.teleport(aboveArenaTeleportLocation);

    }

    public void fillLootChest(Chest chest){
        Inventory chestInventory = chest.getInventory();
        int maxItemsInOneChest = configuration.getInt("maxItemsInOneChest");
        List<?> lootItemsList = configuration.getList("lootItems");

        Random random = new Random();
        for(int i = 0 ; i < (maxItemsInOneChest + 1) ; i++){
            ItemStack randomItem = (ItemStack) lootItemsList.get(random.nextInt(lootItemsList.size() - 1) + 1);
            int randomSlot = random.nextInt(26) + 1;

            boolean itemAlreadyInChest = false;
            for(int slot = 0 ; slot < 27 ; slot++){
                if(chestInventory.getItem(slot) != null){
                    if(chestInventory.getItem(slot).getType() == randomItem.getType())
                        itemAlreadyInChest = true;
                }
            }

            if(!itemAlreadyInChest)
                chestInventory.setItem(randomSlot, randomItem);
        }
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
            Location chestLocation = new Location(currentWorld, randomX - 150, randomY, randomZ - 150);
            System.out.println(chestLocation);
            Location topRightUnderChestLocation = new Location(currentWorld, randomX - 151, randomY - 1, randomZ - 149);

            if(chestLocation.getBlock().getType() == Material.AIR) {

                chestLocation.getBlock().setType(Material.CHEST);
                topRightUnderChestLocation.getBlock().setType(Material.SEA_LANTERN);

                int underChestX = (int) topRightUnderChestLocation.getX();
                int underChestY = (int) topRightUnderChestLocation.getY();
                int underChestZ = (int) topRightUnderChestLocation.getZ();
                for(int x = 0 ; x < 3 ; x++){
                    currentWorld.getBlockAt(underChestX + x, underChestY, underChestZ).setType(Material.SEA_LANTERN);
                    currentWorld.getBlockAt(underChestX + x, underChestY, underChestZ - 1).setType(Material.SEA_LANTERN);
                    currentWorld.getBlockAt(underChestX + x, underChestY, underChestZ - 2).setType(Material.SEA_LANTERN);
                }

                fillLootChest((Chest) currentWorld.getBlockAt(chestLocation).getState());
                for (Player player : Bukkit.getOnlinePlayers())
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Generating Loot Chests: " + i + "/" + amountOfChests));

            }
        }

    }

}