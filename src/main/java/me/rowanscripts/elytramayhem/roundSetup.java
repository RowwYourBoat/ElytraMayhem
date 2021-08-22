package me.rowanscripts.elytramayhem;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class roundSetup extends Settings {

    Location borderLocation = null;
    JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);

    public void findPossibleBorderLocation(Player playerWhoStartedTheGame) {

        File f = new File(plugin.getDataFolder(), "settings.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(f);

        Random random = new Random();
        int randomX = random.nextInt(1000000 - 10000) - 10000;
        int randomZ = random.nextInt(1000000 - 10000) - 10000;
        World currentWorld = playerWhoStartedTheGame.getWorld();
        Location possibleLocation = new Location(currentWorld, randomX, currentWorld.getHighestBlockYAt(randomX, randomZ), randomZ);

        if (settingsData.getBoolean("findBiomeWithLand")){
            if (possibleLocation.getBlock().getType() != Material.WATER)
                borderLocation = possibleLocation;
            else
                findPossibleBorderLocation(playerWhoStartedTheGame);
        } else
            borderLocation = possibleLocation;

    }

    public void teleportPlayersAboveLocation(Player playerWhoStartedTheGame) {

        File f = new File(plugin.getDataFolder(), "settings.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(f);
        World currentWorld = playerWhoStartedTheGame.getWorld();

        WorldBorder border = currentWorld.getWorldBorder();
        border.setCenter(borderLocation);
        border.setSize(settingsData.getInt("borderSize"));

        Location aboveArenaTeleportLocation = new Location(currentWorld, borderLocation.getX(), 300, borderLocation.getZ(), 0, 90);

        for(Player player : Bukkit.getOnlinePlayers())
            player.teleport(aboveArenaTeleportLocation);

    }

    public void fillLootChest(Chest chest, AtomicBoolean specialOccurrence, String specialOccurrenceType){

        File f = new File(plugin.getDataFolder(), "settings.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(f);

        File lootFile = new File(plugin.getDataFolder(), "loot.yml");
        FileConfiguration lootData = YamlConfiguration.loadConfiguration(lootFile);

        Inventory chestInventory = chest.getInventory();
        int maxItemsInOneChest = settingsData.getInt("maxItemsInOneChest");
        List<?> lootItemsList = (List<?>) lootData.get("lootItems");

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

            if(!itemAlreadyInChest) {
                if (specialOccurrence.get() && specialOccurrenceType.equals("OPLoot")){
                    if (randomItem.getType().name().toLowerCase().endsWith("boots") || randomItem.getType().name().toLowerCase().endsWith("leggings") || randomItem.getType().name().toLowerCase().endsWith("chestplate") || randomItem.getType().name().toLowerCase().endsWith("helmet"))
                        randomItem.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                    else if (randomItem.getType().name().toLowerCase().endsWith("sword"))
                        randomItem.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                    else if (randomItem.getType().name().equalsIgnoreCase("crossbow"))
                        randomItem.addEnchantment(Enchantment.QUICK_CHARGE, 3);
                    else if (randomItem.getType().name().equalsIgnoreCase("bow"))
                        randomItem.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
                } else if (lootData.getBoolean("Enchantments")){
                    int chance = random.nextInt(5 - 1) + 1; // 20% enchantment chance
                    if (chance == 1) {
                        int enchantmentLevel = random.nextInt(3 - 1) + 1;
                        if (randomItem.getType().name().toLowerCase().endsWith("boots") || randomItem.getType().name().toLowerCase().endsWith("leggings") || randomItem.getType().name().toLowerCase().endsWith("chestplate") || randomItem.getType().name().toLowerCase().endsWith("helmet"))
                            randomItem.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantmentLevel);
                        else if (randomItem.getType().name().toLowerCase().endsWith("sword"))
                            randomItem.addEnchantment(Enchantment.DAMAGE_ALL, enchantmentLevel);
                        else if (randomItem.getType().name().equalsIgnoreCase("crossbow"))
                            randomItem.addEnchantment(Enchantment.QUICK_CHARGE, enchantmentLevel);
                        else if (randomItem.getType().name().equalsIgnoreCase("bow"))
                            randomItem.addEnchantment(Enchantment.ARROW_DAMAGE, enchantmentLevel);
                    }
                }
                chestInventory.setItem(randomSlot, randomItem);
            }
        }
    }

    public void spawnLootChests(Player playerWhoStartedTheGame, AtomicBoolean specialOccurrence, String specialOccurrenceType) {

        File f = new File(plugin.getDataFolder(), "settings.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(f);
        int amountOfChests = settingsData.getInt("amountOfChests");
        int borderSize = settingsData.getInt("borderSize");

        Random random = new Random();
        World currentWorld = playerWhoStartedTheGame.getWorld();
        for(int i = 0 ; i < (amountOfChests + 1) ; i++){
            int randomX = random.nextInt(((int) borderLocation.getX()+(borderSize/2)) - ((int) borderLocation.getX()-(borderSize/2))) + ((int) borderLocation.getX()+(borderSize/2));
            int randomY = random.nextInt(200 - 125) + 125;
            int randomZ = random.nextInt(((int) borderLocation.getZ()+(borderSize/2)) - ((int) borderLocation.getZ()-(borderSize/2))) + ((int) borderLocation.getZ()+(borderSize/2));
            Location chestLocation = new Location(currentWorld, randomX - borderSize, randomY, randomZ - borderSize);
            Location topRightUnderChestLocation = new Location(currentWorld, randomX - (borderSize + 1), randomY - 1, randomZ - (borderSize - 1));

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

                fillLootChest((Chest) currentWorld.getBlockAt(chestLocation).getState(), specialOccurrence, specialOccurrenceType);
                for (Player player : Bukkit.getOnlinePlayers())
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Generating Loot Chests: " + i + "/" + amountOfChests));

            }
        }

    }

    public void teleportToRandomLocation(Player player){

        File f = new File(plugin.getDataFolder(), "settings.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(f);
        Random random = new Random();
        World currentWorld = player.getWorld();
        int borderSize = settingsData.getInt("borderSize");

        int randomX = random.nextInt(((int) borderLocation.getX()+(borderSize/2)) - ((int) borderLocation.getX()-(borderSize/2))) + ((int) borderLocation.getX()+(borderSize/2));
        int randomZ = random.nextInt(((int) borderLocation.getZ()+(borderSize/2)) - ((int) borderLocation.getZ()-(borderSize/2))) + ((int) borderLocation.getZ()+(borderSize/2));
        Location teleportLocation = new Location(currentWorld, randomX - borderSize, 400, randomZ - borderSize, 0, 90);

        player.teleport(teleportLocation);
    }

}