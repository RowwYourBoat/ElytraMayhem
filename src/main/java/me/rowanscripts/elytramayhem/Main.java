package me.rowanscripts.elytramayhem;

import me.rowanscripts.elytramayhem.getMethods.defaultLootItems;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!getServer().getAllowFlight()){
            getLogger().info(ChatColor.RED + "You need to allow flight in your server.properties file for this plugin to work! The plugin has been disabled automatically!");
            getPluginLoader().disablePlugin(this);
        }
        defaultConfig();
        Bukkit.getPluginCommand("battle").setExecutor(new commands());
        Bukkit.getPluginCommand("battle").setTabCompleter(new ConstructTabComplete());

        int pluginId = 12514;
        Metrics metrics = new Metrics(this, pluginId);
    }

    public void defaultConfig(){
        File settingsFile = new File(this.getDataFolder(), "settings.yml");
        File lootFile = new File(this.getDataFolder(), "loot.yml");
        FileConfiguration settingsData = YamlConfiguration.loadConfiguration(settingsFile);
        FileConfiguration lootData = YamlConfiguration.loadConfiguration(lootFile);
        if (!settingsFile.exists() || !lootFile.exists()) {
            try {
                settingsData.set("findBiomeWithLand", true); // forces the plugin to find a biome with at least some land
                settingsData.set("playersGlow", true); // toggle whether players will glow during rounds
                settingsData.set("specialOccurrences", true); // toggles random weather and time events, like thunder & nighttime.
                settingsData.set("amountOfFireworksAtStart", 3); // how many fireworks each player will receive at the start (limit: 64)
                settingsData.set("borderSize", 150); // the size of the border (minimum: 100, limit: 500)
                settingsData.set("maxItemsInOneChest", 5); // the maximum amount of items in one chest (limit: 27)
                settingsData.set("amountOfChests", 10); // the amount of loot chests that will spawn (limit: 50)
                settingsData.createSection("battleRoyaleMode"); // section
                settingsData.set("battleRoyaleMode.enabled", false); // toggles battle royale mode, where the border shrinks
                settingsData.set("battleRoyaleMode.borderShrinkingDurationInSeconds", 300); // how long it takes for the border to shrink all the way
                settingsData.options().header("Visit the following website for information:\nhttps://github.com/icallhacks/ElytraMayhem/blob/master/README.md");
                lootData.set("Enchantments", true);
                lootData.options().header("There is a 20% chance that an item will be enchanted when Enchantments is true.\n You can add a loot item by copying a different item and editing the value(s). If you mess up & the plugin breaks, use /battle settings reset.");
                defaultLootItems defaultLootItems = new defaultLootItems();
                List<ItemStack> lootItemsList = defaultLootItems.getDefaultLootItems();
                lootData.set("lootItems", lootItemsList);
                settingsData.save(settingsFile);
                lootData.save(lootFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class commands extends game implements CommandExecutor {

        JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);
        boolean registeredEvents = false;

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            File f = new File(plugin.getDataFolder(), "settings.yml");
            FileConfiguration settingsData = YamlConfiguration.loadConfiguration(f);

            if (!registeredEvents) {
                Bukkit.getPluginManager().registerEvents(new eventListener(), plugin);
                registeredEvents = true;
            }

            if (sender instanceof Player){
                if (args.length == 0)
                    return false;
                Player executor = (Player) sender;
                String firstArgument = args[0];

                if (firstArgument.equalsIgnoreCase("start") && executor.hasPermission("elytramayhem.admin")){
                    boolean eligibleForStart = this.startGame(executor);
                    if (!eligibleForStart)
                        executor.sendMessage(ChatColor.RED + "Something went wrong!");
                    else
                        executor.sendMessage(ChatColor.GREEN + "Successfully started the game!");
                } else if (firstArgument.equalsIgnoreCase("stop") && executor.hasPermission("elytramayhem.admin")){
                    this.endGame();
                    executor.sendMessage(ChatColor.RED + "You've forcefully ended the game!");
                } else if (firstArgument.equalsIgnoreCase("settings") && executor.hasPermission("elytramayhem.admin")){
                    return this.settingsManager(executor, args);
                } else if (firstArgument.equalsIgnoreCase("reload") && executor.hasPermission("elytramayhem.admin")) {
                    try {
                        settingsData.load(f);
                        executor.sendMessage(ChatColor.GREEN + "Successfully reloaded the configuration files!");
                    } catch (IOException | InvalidConfigurationException e) {
                        e.printStackTrace();
                    }

                } else if (firstArgument.equalsIgnoreCase("stats")) {
                    playerData playerData = new playerData();
                    if (args.length == 1) {
                        executor.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Your Statistics:\n" + ChatColor.RESET + ChatColor.GRAY +
                                "Wins: " + playerData.get(executor, "Wins") + "\n" +
                                "Kills: " + playerData.get(executor, "Kills") + "\n" +
                                "Deaths: " + playerData.get(executor, "Deaths") + "\n" +
                                "Rounds: " + playerData.get(executor, "Rounds")
                        );
                    } else if (args.length == 2) {
                        String playerName = args[1];
                        Player playerToGetStatsFrom = Bukkit.getPlayer(playerName);
                        if (playerToGetStatsFrom != null){
                            executor.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + playerToGetStatsFrom.getName() + "'s Statistics:\n" + ChatColor.RESET + ChatColor.GRAY +
                                    "Wins: " + playerData.get(playerToGetStatsFrom, "Wins") + "\n" +
                                    "Kills: " + playerData.get(playerToGetStatsFrom, "Kills") + "\n" +
                                    "Deaths: " + playerData.get(playerToGetStatsFrom, "Deaths") + "\n" +
                                    "Rounds: " + playerData.get(playerToGetStatsFrom, "Rounds")
                            );
                        }
                    }
                } else {
                    executor.sendMessage(ChatColor.RED + "Invalid arguments or insufficient permissions!");
                    return true;
                }


            } else if (sender instanceof ConsoleCommandSender){
                System.out.println(ChatColor.RED + "[Elytra Mayhem] This command may not be executed via the console!");
            }

            return true;
        }

    }

    public class ConstructTabComplete implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

            availableArguments arguments = new availableArguments();

            if (sender.hasPermission("elytramayhem.admin")){
                if (args.length == 1){
                    return arguments.getFirstAdminArguments();
                } else if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
                    return arguments.getPlayerArguments();
                } else if (args.length == 2 && args[0].equalsIgnoreCase("settings")){
                    return arguments.getSettingArguments();
                } else if (args.length == 3 && !args[1].equalsIgnoreCase("reset")) {
                    return arguments.getSecondSettingArguments();
                }
            } else {
                if (args.length == 1) {
                    List<String> statsCmd = new ArrayList<>();
                    statsCmd.add("stats");
                    return statsCmd;
                } else if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
                    return arguments.getPlayerArguments();
                }
            }

            return new ArrayList<>();
        }

    }

}