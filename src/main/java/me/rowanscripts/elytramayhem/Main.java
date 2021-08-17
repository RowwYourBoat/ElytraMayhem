package me.rowanscripts.elytramayhem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    FileConfiguration configuration = getConfig();

    @Override
    public void onEnable() {
        configuration.addDefault("findBiomeWithLand", true); // forces the plugin to find a biome with at least some land
        configuration.addDefault("fireworkReceivingDelay", 2400); // the time it takes for the players to receive a new firework (in ticks)
        configuration.addDefault("borderSize", 150); // the size of the border (minimum: 100, limit: 500)
        configuration.addDefault("maxItemsInOneChest", 5); // the maximum amount of items in one chest (limit: 27)
        configuration.addDefault("amountOfChests", 10); // the amount of loot chests that will spawn (limit: 30)
        configuration.addDefault("playersGlow", true); // toggle whether players will glow during rounds
        configuration.options().copyDefaults(true);
        saveConfig();

        Bukkit.getPluginCommand("battle").setExecutor(new commands());
    }

    public class commands extends game implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            if (sender instanceof Player){
                if (args.length == 0)
                    return false;
                Player executor = (Player) sender;
                String firstArgument = args[0];

                if (firstArgument.equalsIgnoreCase("start")){
                    boolean eligibleForStart = this.startGame(executor);
                    if (!eligibleForStart)
                        executor.sendMessage(ChatColor.RED + "Something went wrong!");
                    else
                        executor.sendMessage(ChatColor.GREEN + "Successfully started the game!");
                } else if (firstArgument.equalsIgnoreCase("stop")){

                } else if (firstArgument.equalsIgnoreCase("settings")){

                } else
                    return false;

            } else if (sender instanceof ConsoleCommandSender){
                System.out.println(ChatColor.RED + "[Elytra Mayhem] This command may not be executed via the console!");
            }

            return true;
        }

    }

}