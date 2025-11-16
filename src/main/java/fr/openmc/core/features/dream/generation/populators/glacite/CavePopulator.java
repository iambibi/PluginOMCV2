package fr.openmc.core.features.dream.generation.populators.glacite;

import fr.openmc.core.utils.structure.FeaturesPopulator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.MIN_CAVE_HEIGHT;
import static fr.openmc.core.features.dream.generation.biomes.MudBeachChunkGenerator.MIN_HEIGHT_MUD;

public class CavePopulator extends FeaturesPopulator {

    private final double chunkProbability;
    private final double perSolProbability;

    public CavePopulator(double chunkProbability, double perSolProbability, List<String> features) {
        super("omc_dream", features);
        this.chunkProbability = chunkProbability;
        this.perSolProbability = perSolProbability;
    }

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if (random.nextDouble() >= chunkProbability) return;

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        int attempts = 32;

        for (int i = 0; i < attempts; i++) {
            int x = startX + random.nextInt(16);
            int z = startZ + random.nextInt(16);

            for (int y = MIN_HEIGHT_MUD - 1; y > MIN_CAVE_HEIGHT; y--) {
                Material type = limitedRegion.getType(x, y, z);
                Material below = limitedRegion.getType(x, y - 1, z);

                if (!type.isAir() || !below.equals(Material.ICE)) {
                    Material above = limitedRegion.getType(x, y + 1, z);

                    if (above.isAir() || above.equals(Material.SNOW)) {
                        if (random.nextDouble() < perSolProbability) {
                            Location loc = new Location(limitedRegion.getWorld(), x, y + 1, z);
                            placeFeatures(getRandomFeatures(random), loc, random.nextBoolean(), random.nextBoolean(), false);
                        }
                    }
                }
            }
        }
    }
}
