package me.rowanscripts.elytramayhem;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class game extends roundSetup {

    JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);

    boolean setupInProgress = false;
    boolean gameInProgress = false;
    AtomicBoolean specialOccurrence = new AtomicBoolean(false);
    String specialOccurrenceType = null;
    int timeUntilStart = 15;

    List<UUID> playersInGame = new ArrayList<>();

    BukkitScheduler scheduler = Bukkit.getScheduler();

    public boolean startGame(Player executor) {

        File f = new File(plugin.getDataFolder(), "settings.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(f);
        int amountOfChests = settingsData.getInt("amountOfChests");

        if (setupInProgress || gameInProgress)
            return false;

        setupInProgress = true;
        World currentWorld = executor.getWorld();
        currentWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        currentWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        currentWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        currentWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        currentWorld.setTime(1000);
        for(Player player : Bukkit.getOnlinePlayers()) {
            playersInGame.add(player.getUniqueId());
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(5);
            playerData playerData = new playerData();
            playerData.add(player, "Rounds");
        }

        Bukkit.broadcastMessage(ChatColor.GRAY + "Looking for an appropriate battle location..");
        this.findPossibleBorderLocation(executor);
        Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully found an appropriate battle location!");
        this.teleportPlayersAboveLocation(executor);
        Bukkit.broadcastMessage(ChatColor.GRAY + "Generating loot chests..");
        this.getSpecialOccurrence(settingsData);
        this.spawnLootChests(executor, specialOccurrence, specialOccurrenceType);
        Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully generated " + amountOfChests + " loot chests!");
        Bukkit.broadcastMessage(ChatColor.GRAY + "Finishing up..");

        ItemStack elytra = new ItemStack(Material.ELYTRA);
        ItemStack fireworks = new ItemStack(Material.FIREWORK_ROCKET, settingsData.getInt("amountOfFireworksAtStart"));
        elytra.addEnchantment(Enchantment.DURABILITY, 3);
        for(Player player : Bukkit.getOnlinePlayers()){
            PlayerInventory playerInv = player.getInventory();
            this.teleportToRandomLocation(player);
            player.setGameMode(GameMode.SURVIVAL);
            playerInv.setChestplate(elytra);
            playerInv.setItemInOffHand(fireworks);
        }

        scheduler.scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {

            if (timeUntilStart > 0){
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendTitle(ChatColor.GOLD + "Starting in:", ChatColor.GREEN.toString() + timeUntilStart, 5, 20, 5);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                timeUntilStart--;
            } else if (timeUntilStart == 0 && !gameInProgress) {

                setupInProgress = false;
                gameInProgress = true;
                if (settingsData.getBoolean("battleRoyaleMode.enabled")){
                    WorldBorder border = currentWorld.getWorldBorder();
                    border.setSize(1, settingsData.getLong("battleRoyaleMode.borderShrinkingDurationInSeconds"));
                    border.setDamageBuffer(0);
                }
                for(Player player : Bukkit.getOnlinePlayers()){
                    if (!specialOccurrence.get()) {
                        player.sendTitle(ChatColor.RED + "FIGHT!", "", 5, 20, 5);
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 5, 1);
                    } else if (specialOccurrence.get()) {
                        switch (specialOccurrenceType) {
                            case "DoubleHP":
                                currentWorld.setTime(18000);
                                player.sendTitle(ChatColor.GOLD + "FIGHT!", ChatColor.DARK_PURPLE + "DOUBLE HEALTH EVENT", 5, 40, 5);
                                player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 5, 1);
                                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
                                player.setHealth(40);
                                break;
                            case "HalfHP":
                                currentWorld.setTime(18000);
                                player.sendTitle(ChatColor.GOLD + "FIGHT!", ChatColor.DARK_PURPLE + "HALF HEALTH EVENT", 5, 40, 5);
                                player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 5, 1);
                                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
                                player.setHealth(10);
                                break;
                            case "Thunder":
                                currentWorld.setTime(18000);
                                currentWorld.setStorm(true);
                                currentWorld.setThundering(true);
                                player.sendTitle(ChatColor.GOLD + "FIGHT!", ChatColor.DARK_PURPLE + "SPECIAL WEATHER EVENT", 5, 40, 5);
                                player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 5, 1);
                                break;
                            case "OPLoot":
                                // handled during loot generation
                                currentWorld.setTime(18000);
                                player.sendTitle(ChatColor.GOLD + "FIGHT!", ChatColor.DARK_PURPLE + "OP LOOT EVENT", 5, 40, 5);
                                player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 5, 1);
                                break;
                        }
                    }
                }

            }

            for(Player player : Bukkit.getOnlinePlayers()) {
                if(playersInGame.contains(player.getUniqueId())) {
                    if (settingsData.getBoolean("playersGlow"))
                        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 1, false, false));
                } else
                    player.setGameMode(GameMode.SPECTATOR);
            }

            if(playersInGame.size() == 2 && gameInProgress){
                playerVictory();
                endGame();
            } else if (playersInGame.isEmpty()){
                endGame();
            }

        }, 0, 20);

        return true;
    }

    public void playerVictory(){
        Player playerWhoWon = Bukkit.getPlayer(playersInGame.get(0));
        playerData playerData = new playerData();
        playerData.add(playerWhoWon, "Wins");
        for(Player player : Bukkit.getOnlinePlayers()){
            player.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + playerWhoWon.getName(), ChatColor.BOLD + "has won!", 10, 100, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
        }
    }

    public void endGame(){
        if (!gameInProgress && !setupInProgress)
            return;

        setupInProgress = false;
        gameInProgress = false;
        specialOccurrence.set(false);
        specialOccurrenceType = null;
        scheduler.cancelTasks(JavaPlugin.getPlugin(Main.class));
        playersInGame.clear();
        timeUntilStart = 15;

        for(Player player : Bukkit.getOnlinePlayers()){
            World currentWorld = player.getWorld();
            player.getInventory().clear();
            player.teleport(currentWorld.getSpawnLocation());
            player.setGameMode(GameMode.SURVIVAL);
            player.setFoodLevel(20);
            player.setSaturation(5);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            player.setHealth(20);
            currentWorld.setTime(1000);
            currentWorld.setThundering(false);
            currentWorld.setStorm(false);
            WorldBorder border = currentWorld.getWorldBorder();
            border.setSize(500);
        }
    }

    public void getSpecialOccurrence(FileConfiguration settingsData) {
        Random random = new Random();
        int randomValue = 1; //random.nextInt(11 - 1) + 1;
        specialOccurrence.set(randomValue == 1 && settingsData.getBoolean("specialOccurrences"));

        if (specialOccurrence.get()){
            int occurrenceNumber = random.nextInt(5 - 1) + 1;
            if (occurrenceNumber == 1) {
                specialOccurrenceType = "Thunder";
            } else if (occurrenceNumber == 2) {
                specialOccurrenceType = "DoubleHP";
            } else if (occurrenceNumber == 3) {
                specialOccurrenceType = "HalfHP";
            } else {
                specialOccurrenceType = "OPLoot";
            }
        }

    }

    public class eventListener implements Listener {

        @EventHandler(priority = EventPriority.LOW)
        public void blockPlayerMovementDuringSetup(PlayerMoveEvent event){
            if (setupInProgress)
                event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void removePlayerFromListAfterDeath(PlayerDeathEvent event){
            Player playerWhoDied = event.getEntity();
            Player playerWhoKilled = event.getEntity().getKiller();
            if (gameInProgress || setupInProgress){
                playerData playerData = new playerData();
                playersInGame.remove(playerWhoDied.getUniqueId());
                playerData.add(playerWhoDied, "Deaths");
                if (playerWhoKilled != null)
                    playerData.add(playerWhoKilled, "Kills");
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void removePlayerFromListAfterLeave(PlayerQuitEvent event){
            Player player = event.getPlayer();
            if (gameInProgress || setupInProgress){
                playersInGame.remove(player.getUniqueId());
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void stopPlayerFromBreakingChests(BlockBreakEvent event) {
            if (!gameInProgress)
                return;
            Material blockType = event.getBlock().getType();
            if (blockType == Material.SEA_LANTERN || blockType == Material.CHEST){
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void bedrockOnChestOpen(InventoryOpenEvent event) {
            if (!gameInProgress)
                return;
            InventoryHolder holder = event.getInventory().getHolder();
            Player playerWhoOpenedTheChest = (Player) event.getPlayer();
            if (holder instanceof Chest){
                World currentWorld = playerWhoOpenedTheChest.getWorld();
                Location chestLocation = ((Chest) holder).getLocation();
                currentWorld.getBlockAt((int) chestLocation.getX(), (int) chestLocation.getY() - 1, (int) chestLocation.getZ()).setType(Material.BEDROCK);
                currentWorld.getBlockAt((int) chestLocation.getX() + 1, (int) chestLocation.getY() - 1, (int) chestLocation.getZ()).setType(Material.BEDROCK);
                currentWorld.getBlockAt((int) chestLocation.getX() - 1, (int) chestLocation.getY() - 1, (int) chestLocation.getZ()).setType(Material.BEDROCK);
                currentWorld.getBlockAt((int) chestLocation.getX(), (int) chestLocation.getY() - 1, (int) chestLocation.getZ() + 1).setType(Material.BEDROCK);
                currentWorld.getBlockAt((int) chestLocation.getX(), (int) chestLocation.getY() - 1, (int) chestLocation.getZ() - 1).setType(Material.BEDROCK);
            }
        }

    }

}