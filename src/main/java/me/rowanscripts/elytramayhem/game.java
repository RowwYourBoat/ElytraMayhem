package me.rowanscripts.elytramayhem;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class game extends roundSetup {

    boolean setupInProgress = false;
    boolean gameInProgress = false;

    List<UUID> playersInGame = new ArrayList<>();

    public boolean startGame(Player executor) {
        Bukkit.getPluginManager().registerEvents(new eventListener(), JavaPlugin.getPlugin(Main.class));
        if (setupInProgress || gameInProgress)
            return false;

        setupInProgress = true;
        for(Player player : Bukkit.getOnlinePlayers()) {
            playersInGame.add(player.getUniqueId());
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);
        }

        Bukkit.broadcastMessage(ChatColor.GRAY + "Looking for an appropriate battle location..");
        this.findPossibleBorderLocation(executor);
        Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully found an appropriate battle location!");
        this.teleportPlayersAboveLocation(executor);
        Bukkit.broadcastMessage(ChatColor.GRAY + "Generating loot chests..");
        this.spawnLootChests(executor);

        return true;
    }

    public class eventListener implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        public void blockPlayerMovementDuringSetup(PlayerMoveEvent event){
            if (setupInProgress)
                event.setCancelled(true);
        }

    }

}
