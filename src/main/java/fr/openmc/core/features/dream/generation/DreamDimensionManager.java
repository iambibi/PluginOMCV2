package fr.openmc.core.features.dream.generation;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.biomes.*;
import fr.openmc.core.features.dream.generation.effects.BiomeParticleListener;
import fr.openmc.core.features.dream.generation.populators.glacite.GlaciteGeodePopulator;
import fr.openmc.core.features.dream.generation.populators.glacite.GroundSpikePopulator;
import fr.openmc.core.features.dream.generation.populators.glacite.VerticalSpikePopulator;
import fr.openmc.core.features.dream.generation.populators.mud.RockPopulator;
import fr.openmc.core.features.dream.generation.populators.plains.PlainsTreePopulator;
import fr.openmc.core.features.dream.generation.populators.soulforest.SoulTreePopulator;
import fr.openmc.core.features.dream.generation.structures.cloud.CloudCastleStructure;
import fr.openmc.core.features.dream.generation.structures.glacite.BaseCampStructure;
import fr.openmc.core.utils.SchematicsUtils;
import fr.openmc.core.utils.StructureUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DreamDimensionManager {

    public static final String DIMENSION_NAME = "world_dream";
    private final OMCPlugin plugin;

    public DreamDimensionManager() {
        this.plugin = OMCPlugin.getInstance();

        // ** STRUCTURES SCHEMATICS REGISTER **
        SchematicsUtils.extractSchematic(CloudCastleStructure.schemCloudCastleName);
        SchematicsUtils.extractSchematic(BaseCampStructure.schemBaseCampName);

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

        File worldFolder = new File(Bukkit.getWorldContainer(), DIMENSION_NAME);
        long seed;

        if (!worldFolder.exists()) {
            seed = createSeed();
            creator.seed(seed);
            plugin.getLogger().info("New Dream world created with seed: " + seed);
        } else {
            World existing = Bukkit.getWorld(DIMENSION_NAME);
            seed = (existing != null) ? existing.getSeed() : creator.seed();
            plugin.getLogger().info("Loading existing Dream world with seed: " + seed);
        }

        creator.generator(new DreamChunkGenerator(seed));
        new SoulForestChunkGenerator(seed);
        new PlainsChunkGenerator(seed);
        new MudBeachChunkGenerator(seed);
        new CloudChunkGenerator(seed);
        new GlaciteCaveChunkGenerator(seed);
        creator.environment(World.Environment.NORMAL);

        World dream = creator.createWorld();

        // ** POPULATORS REGISTER **
        dream.getPopulators().add(new RockPopulator());
        dream.getPopulators().add(new PlainsTreePopulator());
        dream.getPopulators().add(new SoulTreePopulator());
        dream.getPopulators().add(new VerticalSpikePopulator());
        dream.getPopulators().add(new GroundSpikePopulator());
        dream.getPopulators().add(new GlaciteGeodePopulator());

        // ** STRUCTURES POPULATORS REGISTER **
        dream.getPopulators().add(new CloudCastleStructure());
        dream.getPopulators().add(new BaseCampStructure());

        // ** SET GAMERULE FOR THE WORLD **
        dream.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        dream.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        dream.setGameRule(GameRule.DISABLE_RAIDS, true);
        dream.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        dream.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        dream.setGameRule(GameRule.NATURAL_REGENERATION, false);

        dream.setTime(18000);

        plugin.getLogger().info("Dream Dimension ready!");
    }

    private void preloadAllStructures() {
        Map<String, List<String>> structuresByGroup = new HashMap<>();

        structuresByGroup.put("omc_dream", List.of(
                "cave1.nbt",
                "cave2.nbt",
                "cave3.nbt"
        ));

        StructureUtils.preloadStructures(structuresByGroup);
    }

    private long createSeed() {
        Random random = new Random();

        long seed = random.nextLong();

        while (seed == 0) {
            seed = random.nextLong();
        }

        return seed;
    }

    public static DreamBiome getDreamBiome(Biome biome) {
        for (DreamBiome dreamBiome : DreamBiome.values()) {
            if (!dreamBiome.getBiome().equals(biome)) continue;

            return dreamBiome;
        }

        return DreamBiome.SCULK_PLAINS;
    }

    public static DreamBiome getDreamBiome(Player player) {
        World world = player.getWorld();

        if (!world.getName().equals(DIMENSION_NAME)) return null;

        return getDreamBiome(world.getBiome(player.getLocation()));
    }
}

