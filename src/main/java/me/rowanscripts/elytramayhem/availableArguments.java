package me.rowanscripts.elytramayhem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class availableArguments {

    public List<String> getFirstAdminArguments() {
        List<String> firstArguments = new ArrayList<>();

        firstArguments.add("start");
        firstArguments.add("stop");
        firstArguments.add("stats");
        firstArguments.add("settings");
        firstArguments.add("reload");

        return firstArguments;
    }

    public List<String> getSettingArguments() {
        List<String> settingsArguments = new ArrayList<>();

        settingsArguments.add("findBiomeWithLand");
        settingsArguments.add("playersGlow");
        settingsArguments.add("battleRoyaleMode");
        settingsArguments.add("amountOfFireworksAtStart");
        settingsArguments.add("borderSize");
        settingsArguments.add("maxItemsInOneChest");
        settingsArguments.add("amountOfChests");
        settingsArguments.add("reset");

        return settingsArguments;
    }

    public List<String> getSecondSettingArguments() {
        List<String> settingsArguments = new ArrayList<>();

        settingsArguments.add("set");
        settingsArguments.add("get");

        return settingsArguments;
    }

    public List<String> getPlayerArguments() {
        List<String> playerArguments = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers())
            playerArguments.add(player.getName());

        return playerArguments;
    }

}
