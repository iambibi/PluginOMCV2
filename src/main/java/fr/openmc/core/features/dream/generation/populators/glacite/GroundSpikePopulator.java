package fr.openmc.core.features.dream.generation.populators.glacite;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.utils.StructureUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.MAX_CAVE_HEIGHT;
import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.MIN_CAVE_HEIGHT;


public class GroundSpikePopulator extends BlockPopulator {
    private static final double CHUNK_SPIKE_PROBABILITY = 0.6;

    private static final double PER_SOL_PROBABILITY = 0.01;

    private static final List<String> GROUND_SPIKE_FEATURES = new ArrayList<>(List.of(
            "glacite/spike_normal_1",
            "glacite/spike_normal_2",
            "glacite/spike_normal_3",
            "glacite/spike_normal_4"
    ));

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (random.nextDouble() >= CHUNK_SPIKE_PROBABILITY) return;

        int startX = chunk.getX() << 4;
        int startZ = chunk.getZ() << 4;

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = startX + dx;
                int z = startZ + dz;

                for (int y = MAX_CAVE_HEIGHT - 1; y > MIN_CAVE_HEIGHT; y--) {
                    Block below = world.getBlockAt(x, y - 1, z);
                    if (below.getType() == Material.ICE) continue;

                    Block block = world.getBlockAt(x, y, z);

                    if (!block.getType().isAir()) {
                        Block above = world.getBlockAt(x, y + 1, z);
                        if (above.getType().isAir()
                                || above.getType() == Material.SNOW) {

                            Location loc = new Location(world, x, y + 1, z);

                            if (world.getBiome(loc).equals(DreamBiome.GLACITE_GROTTO.getBiome())) {
                                if (random.nextDouble() < PER_SOL_PROBABILITY) {
                                    try {
                                        StructureUtils.placeStructure(
                                                StructureUtils.getStructureFile("omc_dream",
                                                        GROUND_SPIKE_FEATURES.get(random.nextInt(GROUND_SPIKE_FEATURES.size()))),
                                                loc,
                                                random.nextBoolean(),
                                                random.nextBoolean()
                                        );
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}