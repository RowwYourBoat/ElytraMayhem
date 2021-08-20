package me.rowanscripts.elytramayhem;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class game extends roundSetup {

    JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);

    boolean setupInProgress = false;
    boolean gameInProgress = false;
    int timeUntilStart = 15;

    List<UUID> playersInGame = new ArrayList<>();

    BukkitScheduler scheduler = Bukkit.getScheduler();

    public boolean startGame(Player executor) {

        File f = new File(plugin.getDataFolder(), "settings.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(f);
        int amountOfChests = settingsData.getInt("amountOfChests");

        Bukkit.getPluginManager().registerEvents(new eventListener(), JavaPlugin.getPlugin(Main.class));
        if (setupInProgress || gameInProgress)
            return false;

        setupInProgress = true;
        for(Player player : Bukkit.getOnlinePlayers()) {
            playersInGame.add(player.getUniqueId());
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(5);
        }

        Bukkit.broadcastMessage(ChatColor.GRAY + "Looking for an appropriate battle location..");
        this.findPossibleBorderLocation(executor);
        Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully found an appropriate battle location!");
        this.teleportPlayersAboveLocation(executor);
        Bukkit.broadcastMessage(ChatColor.GRAY + "Generating loot chests..");
        this.spawnLootChests(executor);
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
                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendTitle(ChatColor.RED + "FIGHT!", "", 5, 20, 5);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
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
            }

        }, 0, 20);

        return true;
    }

    public void playerVictory(){
        Player playerWhoWon = Bukkit.getPlayer(playersInGame.get(0));
        for(Player player : Bukkit.getOnlinePlayers()){
            player.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + playerWhoWon.getName(), ChatColor.BOLD + "has won!", 10, 100, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
        }
    }

    public void endGame(){
        if (!gameInProgress && !setupInProgress)
            return;

        scheduler.cancelTasks(JavaPlugin.getPlugin(Main.class));
        playersInGame.clear();
        setupInProgress = false;
        gameInProgress = false;
        timeUntilStart = 15;

        for(Player player : Bukkit.getOnlinePlayers()){
            World currentWorld = player.getWorld();
            player.getInventory().clear();
            player.teleport(currentWorld.getSpawnLocation());
            player.setGameMode(GameMode.SURVIVAL);
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
            Player player = event.getEntity();
            if (gameInProgress || setupInProgress){
                playersInGame.remove(player.getUniqueId());
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void removePlayerFromListAfterLeave(PlayerQuitEvent event){
            Player player = event.getPlayer();
            if (gameInProgress || setupInProgress){
                playersInGame.remove(player.getUniqueId());
            }
        }

    }

}