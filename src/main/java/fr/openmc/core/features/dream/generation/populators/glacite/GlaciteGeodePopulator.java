package fr.openmc.core.features.dream.generation.populators.glacite;

import fr.openmc.core.utils.structure.FeaturesPopulator;
import fr.openmc.core.utils.structure.StructureUtils;
import org.bukkit.Location;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.MIN_CAVE_HEIGHT;
import static fr.openmc.core.features.dream.generation.biomes.MudBeachChunkGenerator.MIN_HEIGHT_MUD;


public class GlaciteGeodePopulator extends FeaturesPopulator {
    private static final double CHUNK_GEODE_PROBABILITY = 0.1;

    public GlaciteGeodePopulator() {
        super("omc_dream", List.of("glacite/geode"));
    }

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if (random.nextDouble() >= CHUNK_GEODE_PROBABILITY) return;

        int x = (chunkX << 4) + random.nextInt(16);
        int z = (chunkZ << 4) + random.nextInt(16);
        int y = MIN_CAVE_HEIGHT + random.nextInt(MIN_HEIGHT_MUD - MIN_CAVE_HEIGHT);

        Location loc = new Location(limitedRegion.getWorld(), x, y, z);

        StructureUtils.CachedStructure structure = getRandomFeatures(random);
        placeFeatures(structure, loc, random.nextBoolean(), random.nextBoolean(), true);
    }
}
