package fr.openmc.core.features.dream.generation;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.biomes.*;
import fr.openmc.core.features.dream.generation.effects.BiomeParticleListener;
import fr.openmc.core.features.dream.generation.populators.forest.PillarPopulator;
import fr.openmc.core.features.dream.generation.populators.mud.RockPopulator;
import fr.openmc.core.features.dream.generation.populators.plains.PlainsTreePopulator;
import fr.openmc.core.features.dream.generation.structures.cloud.CloudCastleStructure;
import fr.openmc.core.utils.SchematicsUtils;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.Random;

public class DreamDimensionManager {

    public static final String DIMENSION_NAME = "world_dream";
    private final OMCPlugin plugin;

    public DreamDimensionManager() {
        this.plugin = OMCPlugin.getInstance();

        // ** STRUCTURES SCHEMATICS REGISTER **
        SchematicsUtils.extractSchematic(CloudCastleStructure.schemCloudCastleName);

        // ** DIMENSION INIT **
        OMCPlugin.registerEvents(
                new BiomeParticleListener()
        );
        init();
    }

    public void init() {
        createDimension();
    }

    public void createDimension() {
        WorldCreator creator = new WorldCreator(DIMENSION_NAME);
        long seed = createSeed();

        // ** BIOMES REGISTER **
        new SoulForestChunkGenerator(seed);
        new PlainsChunkGenerator(seed);
        new MudBeachChunkGenerator(seed);
        new CloudChunkGenerator(seed);
        new GlaciteCaveChunkGenerator(seed);

        creator.seed(seed);
        creator.generator(new DreamChunkGenerator(seed));
        creator.environment(World.Environment.NORMAL);

        System.out.println(creator.seed());
        World dream = creator.createWorld();

        // ** POPULATORS REGISTER **
        dream.getPopulators().add(new RockPopulator());
        dream.getPopulators().add(new PlainsTreePopulator());
        dream.getPopulators().add(new PillarPopulator());

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

