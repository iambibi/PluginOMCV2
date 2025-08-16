package fr.openmc.core.features.dream.generation;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.biomes.*;
import fr.openmc.core.features.dream.generation.populators.mud.RockPopulator;
import fr.openmc.core.features.dream.generation.populators.plains.PlainsTreePopulator;
import fr.openmc.core.features.dream.generation.structures.cloud.CloudCastleStructure;
import fr.openmc.core.utils.SchematicsUtils;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.Random;

public class DreamDimensionManager {

    private final OMCPlugin plugin;

    public DreamDimensionManager() {
        this.plugin = OMCPlugin.getInstance();

        // ** BIOMES REGISTER **
        new SoulForestChunkGenerator();
        new PlainsChunkGenerator();
        new MudBeachChunkGenerator();
        new CloudChunkGenerator();
        new GlaciteCaveChunkGenerator();

        // ** STRUCTURES SCHEMATICS REGISTER **
        SchematicsUtils.extractSchematic(CloudCastleStructure.schemCloudCastleName);

        init();
    }

    public void init() {
        createDimension();
    }

    public void createDimension() {
        WorldCreator creator = new WorldCreator("world_dream");
        creator.biomeProvider(new DreamBiomeProvider(createSeed()));
        creator.generator(new DreamChunkGenerator());
        creator.environment(World.Environment.NORMAL);
        World dream = creator.createWorld();

        // ** POPULATORS REGISTER **
        dream.getPopulators().add(new RockPopulator());
        dream.getPopulators().add(new PlainsTreePopulator());

        // ** STRUCTURES POPULATORS REGISTER **
        dream.getPopulators().add(new CloudCastleStructure());

        dream.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        dream.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        dream.setGameRule(GameRule.DISABLE_RAIDS, true);
        dream.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        dream.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        dream.setGameRule(GameRule.NATURAL_REGENERATION, false);

        dream.setTime(18000);

        plugin.getLogger().info("Dream Dimension created successfully!");
    }

    private long createSeed() {
        Random random = new Random();

        long seed = random.nextLong();

        while (seed == 0) {
            seed = random.nextLong();
        }

        return seed;
    }
}

