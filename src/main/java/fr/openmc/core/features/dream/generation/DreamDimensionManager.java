package fr.openmc.core.features.dream.generation;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.biomes.*;
import fr.openmc.core.features.dream.generation.populators.RockPopulator;
import org.bukkit.*;

import java.util.Random;

public class DreamDimensionManager {

    OMCPlugin plugin;
    Server server;

    public DreamDimensionManager() {
        this.plugin = OMCPlugin.getInstance();
        this.server = Bukkit.getServer();

        // ** BIOMES REGISTER **
        new SoulForestChunkGenerator();
        new PlainsChunkGenerator();
        new MudBeachChunkGenerator();
        new CloudChunkGenerator();
        new GlaciteCaveChunkGenerator();

        // ** POPULATOR **
        try {
            new RockPopulator();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load rock populator: " + e.getMessage());
            e.printStackTrace();
        }
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

        dream.getPopulators().add(new RockPopulator());

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

