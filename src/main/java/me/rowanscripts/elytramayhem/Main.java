package me.rowanscripts.elytramayhem;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
        Settings settings = new Settings();
        settings.defaultConfig(false);
        Bukkit.getPluginCommand("battle").setExecutor(new commands());
        Bukkit.getPluginCommand("battle").setTabCompleter(new ConstructTabComplete());
        int pluginId = 12514;
        Metrics metrics = new Metrics(this, pluginId);
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