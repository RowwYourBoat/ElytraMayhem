package me.rowanscripts.elytramayhem;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class game extends settings {

    public boolean startGame(Player executor) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
        }



        return false;
    }

}
