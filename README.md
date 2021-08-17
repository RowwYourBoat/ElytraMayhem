# Elytra Mayhem
This is a Minigame Plugin, where all players will be teleported to a random location on the world where they'll have to fight to the death using the items in the loot chests. There's a catch though; every player will have an elytra (and some fireworks) and the loot chests will spawn in the air. The last man standing wins. Good luck, and have fun!

## How to get started
Once you've gathered a few friends to play with (a minimum of 3 players is recommended), you can run the command /battle start. The plugin will then look for a battle location (with at least some land and not only water) and generate the loot chests. Once this process is completed (it may take some time depending on the configured settings), every player will spawn at a random location within the world border with an elytra (in the air) so that they can loot the chests and fight!

## Settings / Configuration
You're able to configure the game in-game with the command /battle settings or via the config file. The configurable settings are:

findBiomeWithLand - Determine whether you want the plugin to look for a biome with at least some land or not. Disabling this may reduce the duration of the setup process.

playersGlow - Determines whether players will glow during a round.

amountOfFireworksAtStart - How many fireworks each player will receive at the start. (limit: 64)

borderSize - The size of the border. (minimum: 100, limit: 500)

maxItemsInOneChest - The maximum amount of items in one chest. (limit: 27)

amountOfChests - The amount of loot chests that will spawn. (limit: 30)

To reset the configuration file, you can delete it and reload the server. Keep in mind that if you also have other plugins installed, reloading them might cause issues.
