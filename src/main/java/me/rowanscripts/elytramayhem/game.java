package me.rowanscripts.elytramayhem;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
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

    boolean devMode = true;

    JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);

    boolean setupInProgress = false;
    boolean gameInProgress = false;
    int timeUntilStart = 15;

    AtomicBoolean specialOccurrence = new AtomicBoolean(false);
    String specialOccurrenceType = null;
    int lastSpecialOccurrenceNumber = 0;

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

        timeUntilStart = settingsData.getInt("countdownDuration");

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
                            case "SlowFalling":
                                currentWorld.setTime(18000);
                                player.sendTitle(ChatColor.GOLD + "FIGHT!", ChatColor.DARK_PURPLE + "SLOW FALLING EVENT", 5, 40, 5);
                                player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 5, 1);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 99999, 0));
                                break;
                            case "OnlyCrossbow":
                                currentWorld.setTime(18000);
                                player.sendTitle(ChatColor.GOLD + "FIGHT!", ChatColor.DARK_PURPLE + "CROSSBOW EVENT", 5, 40, 5);
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

            if(playersInGame.size() == 1 && gameInProgress && !devMode){
                playerVictory();
                endGame(currentWorld);
            } else if (playersInGame.isEmpty()){
                endGame(currentWorld);
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

    public void endGame(World currentWorld){
        if (!gameInProgress && !setupInProgress)
            return;

        File f = new File(plugin.getDataFolder(), "settings.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(f);

        setupInProgress = false;
        gameInProgress = false;
        specialOccurrence.set(false);
        specialOccurrenceType = null;
        scheduler.cancelTasks(JavaPlugin.getPlugin(Main.class));
        playersInGame.clear();

        currentWorld.setTime(1000);
        currentWorld.setThundering(false);
        currentWorld.setStorm(false);
        WorldBorder border = currentWorld.getWorldBorder();
        border.setSize(settingsData.getInt("borderSize"));

        for(Player player : Bukkit.getOnlinePlayers()){

            for(PotionEffect effect : player.getActivePotionEffects())
                if(player.hasPotionEffect(effect.getType()))
                    player.removePotionEffect(effect.getType());

            player.getInventory().clear();
            player.teleport(currentWorld.getSpawnLocation());
            player.setGameMode(GameMode.SURVIVAL);
            player.setFoodLevel(20);
            player.setSaturation(5);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            player.setHealth(20);

        }

        for(Entity entityOnGround : currentWorld.getEntities())
            if (entityOnGround instanceof Item || entityOnGround instanceof ItemStack)
                entityOnGround.remove();

    }

    public void getSpecialOccurrence(FileConfiguration settingsData) {
        if(!settingsData.getBoolean("specialOccurrences.enabled"))
            return;

        Random random = new Random();
        int randomValue = random.nextInt(11 - 1) + 1;
        specialOccurrence.set(settingsData.getBoolean("specialOccurrences.everyRound") || randomValue == 1);

        if (specialOccurrence.get()) {

            for(int i = 0 ; i < 20 ; i++) {

                int occurrenceNumber = random.nextInt(7 - 1) + 1;

                if (occurrenceNumber != lastSpecialOccurrenceNumber) {
                    if (occurrenceNumber == 1 && settingsData.getBoolean("specialOccurrences.occurrences.Thunder")) {
                        specialOccurrenceType = "Thunder";
                    } else if (occurrenceNumber == 2 && settingsData.getBoolean("specialOccurrences.occurrences.DoubleHP")) {
                        specialOccurrenceType = "DoubleHP";
                    } else if (occurrenceNumber == 3 && settingsData.getBoolean("specialOccurrences.occurrences.HalfHP")) {
                        specialOccurrenceType = "HalfHP";
                    } else if (occurrenceNumber == 4 && settingsData.getBoolean("specialOccurrences.occurrences.OPLoot")) {
                        specialOccurrenceType = "OPLoot";
                    } else if (occurrenceNumber == 5 && settingsData.getBoolean("specialOccurrences.occurrences.SlowFalling")) {
                        specialOccurrenceType = "SlowFalling";
                    } else if (settingsData.getBoolean("specialOccurrences.occurrences.OnlyCrossbow"))
                        specialOccurrenceType = "OnlyCrossbow";

                    if (specialOccurrenceType != null){
                        lastSpecialOccurrenceNumber = occurrenceNumber;
                        break;
                    }
                }

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

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onCrossbowShot(EntityShootBowEvent event) {
            if (!gameInProgress)
                return;
            if (event.getBow().getType() == Material.CROSSBOW && event.getEntity() instanceof Player){
                Player playerWhoShotTheCrossbow = (Player) event.getEntity();
                if (event.getProjectile().getType() == EntityType.FIREWORK){
                    Firework firework = (Firework) event.getProjectile();
                    if (!firework.getFireworkMeta().hasDisplayName()) {
                        event.setCancelled(true);
                        playerWhoShotTheCrossbow.sendMessage(ChatColor.RED + "You can't fire regular/super fireworks with a crossbow!");
                        return;
                    }
                    String customName = firework.getFireworkMeta().getDisplayName();
                    if (!customName.equals(ChatColor.RED + "Crossbow Ammo")){
                        event.setCancelled(true);
                        playerWhoShotTheCrossbow.sendMessage(ChatColor.RED + "You can't fire regular/super fireworks with a crossbow!");
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void blockElytraBoost(PlayerInteractEvent event) {
            if (!gameInProgress)
                return;
            Player player = event.getPlayer();
            if (player.isGliding()){
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR){
                    if(player.getInventory().getItemInMainHand().getType() == Material.FIREWORK_ROCKET || player.getInventory().getItemInOffHand().getType() == Material.FIREWORK_ROCKET) {
                        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
                        ItemStack offHandItem = player.getInventory().getItemInOffHand();

                        JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);
                        plugin.getLogger().info(mainHandItem + " : " + offHandItem);

                        if (mainHandItem.getType() != Material.AIR) {
                            ItemMeta mainHandMeta = mainHandItem.getItemMeta();
                            if (mainHandMeta.hasDisplayName()) {
                                if (mainHandMeta.getDisplayName().equals(ChatColor.RED + "Crossbow Ammo")) {
                                    event.setCancelled(true);
                                    player.sendMessage(ChatColor.RED + "You aren't able to boost yourself with crossbow ammo!");
                                }
                            }

                        } else if (offHandItem.getType() != Material.AIR) {
                            ItemMeta offHandMeta = offHandItem.getItemMeta();
                            if (offHandMeta.hasDisplayName()) {
                                if (offHandMeta.getDisplayName().equals(ChatColor.RED + "Crossbow Ammo")) {
                                    event.setCancelled(true);
                                    player.sendMessage(ChatColor.RED + "You aren't able to boost yourself with crossbow ammo!");
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}