package fr.openmc.core.features.limbo.dimension;

import fr.openmc.core.OMCPlugin;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import static fr.openmc.core.features.limbo.LimboManager.limboSpawn;
import static fr.openmc.core.features.limbo.LimboManager.limboWorld;

public class LimboDimensionManager {

    public static void createLimboDimension() {
        WorldCreator creator = new WorldCreator("world_limbo");
        creator.generator(new EmptyChunkGenerator());
        creator.environment(World.Environment.THE_END);
        creator.hardcore(true);
        limboWorld = creator.createWorld();

        limboSpawn = new Location(limboWorld, 222, 100, 222);

        limboWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        limboWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        limboWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        limboWorld.setGameRule(GameRule.DISABLE_RAIDS, true);
        limboWorld.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        limboWorld.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        limboWorld.setGameRule(GameRule.NATURAL_REGENERATION, false);

        limboWorld.setTime(18000);
        limboWorld.setSpawnLocation(limboSpawn);

        OMCPlugin.getInstance().getLogger().info("Limbo World created successfully!");
    }
}
