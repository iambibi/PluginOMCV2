package fr.openmc.core.features.dream.generation.populators.soulforest;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.utils.structure.FeaturesPopulator;
import fr.openmc.core.utils.structure.StructureUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;


public class PillarPopulator extends FeaturesPopulator {
    private static final double PILLAR_PROBABILITY = 0.07;

    public PillarPopulator() {
        super("omc_dream", List.of(
                "soul_forest/pillar"
        ));
    }

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if (random.nextDouble() >= PILLAR_PROBABILITY) return;

        World world = limitedRegion.getWorld();
        int x = (chunkX << 4) + random.nextInt(16);
        int z = (chunkZ << 4) + random.nextInt(16);
        int y = world.getHighestBlockYAt(x, z);

        Location loc = new Location(world, x, y, z);

        if (!world.getBiome(loc).equals(DreamBiome.SOUL_FOREST.getBiome())) return;

        StructureUtils.CachedStructure structure = getRandomFeatures(random);
        placeFeatures(structure, loc, false, false, false);
    }
}