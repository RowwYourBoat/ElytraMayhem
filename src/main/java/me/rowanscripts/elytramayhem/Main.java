package me.rowanscripts.elytramayhem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    FileConfiguration configuration = getConfig();

    @Override
    public void onEnable() {
        configuration.addDefault("findBiomeWithLand", true); // forces the plugin to find a biome with at least some land
        configuration.addDefault("playersGlow", true); // toggle whether players will glow during rounds
        configuration.addDefault("amountOfFireworksAtStart", 3); // how many fireworks each player will receive at the start (limit: 64)
        configuration.addDefault("borderSize", 150); // the size of the border (minimum: 100, limit: 500)
        configuration.addDefault("maxItemsInOneChest", 5); // the maximum amount of items in one chest (limit: 27)
        configuration.addDefault("amountOfChests", 10); // the amount of loot chests that will spawn (limit: 30)
        List lootItemsList = getDefaultLootItems();
        configuration.addDefault("lootItems", lootItemsList);
        configuration.options().copyDefaults(true);
        saveConfig();
        Bukkit.getPluginCommand("battle").setExecutor(new commands());
    }

    public List getDefaultLootItems(){
        List<ItemStack> lootItems = new ArrayList<>();

        lootItems.add(new ItemStack(Material.WOODEN_SWORD));
        lootItems.add(createItemStackWithEnchantment(Material.WOODEN_SWORD, Enchantment.DAMAGE_ALL, 2));
        lootItems.add(new ItemStack(Material.STONE_SWORD));
        lootItems.add(createItemStackWithEnchantment(Material.STONE_SWORD, Enchantment.DAMAGE_ALL, 2));
        lootItems.add(new ItemStack(Material.IRON_SWORD));
        lootItems.add(createItemStackWithEnchantment(Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 2));

        lootItems.add(new ItemStack(Material.WOODEN_AXE, 1));
        lootItems.add(new ItemStack(Material.STONE_AXE, 1));

        lootItems.add(new ItemStack(Material.BOW));
        lootItems.add(new ItemStack(Material.ARROW, 5));
        lootItems.add(new ItemStack(Material.ARROW, 10));

        lootItems.add(new ItemStack(Material.LEATHER_BOOTS));
        lootItems.add(new ItemStack(Material.CHAINMAIL_BOOTS));
        lootItems.add(new ItemStack(Material.IRON_BOOTS));
        lootItems.add(new ItemStack(Material.LEATHER_LEGGINGS));
        lootItems.add(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        lootItems.add(new ItemStack(Material.IRON_LEGGINGS));
        lootItems.add(new ItemStack(Material.LEATHER_CHESTPLATE));
        lootItems.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        lootItems.add(new ItemStack(Material.IRON_CHESTPLATE));
        lootItems.add(new ItemStack(Material.LEATHER_HELMET));
        lootItems.add(new ItemStack(Material.CHAINMAIL_HELMET));
        lootItems.add(new ItemStack(Material.IRON_HELMET));

        lootItems.add(new ItemStack(Material.COOKED_BEEF, 8));
        lootItems.add(new ItemStack(Material.BREAD, 16));
        lootItems.add(new ItemStack(Material.COOKED_PORKCHOP, 8));

        lootItems.add(new ItemStack(Material.ENDER_PEARL));
        lootItems.add(new ItemStack(Material.FIREWORK_ROCKET));

        return lootItems;
    }

    public ItemStack createItemStackWithEnchantment(Material item, Enchantment enchantment, Integer level){
        ItemStack enchantedItem = new ItemStack(item);
        enchantedItem.addEnchantment(enchantment, level);

        return enchantedItem;
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
                    this.endGame();
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