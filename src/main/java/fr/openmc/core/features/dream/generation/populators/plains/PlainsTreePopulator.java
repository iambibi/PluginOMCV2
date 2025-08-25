package fr.openmc.core.features.dream.generation.populators.plains;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.utils.StructureUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static fr.openmc.core.features.dream.generation.biomes.PlainsChunkGenerator.PLAINS_SURFACE_MATERIAL;


public class PlainsTreePopulator extends BlockPopulator {
    private static final double TREE_PROBABILITY = 0.08;
    private static final List<String> TREE_FEATURES = new ArrayList<>(List.of(
            "plains/tree_1",
            "plains/tree_2",
            "plains/tree_3",
            "plains/tree_4",
            "plains/tree_5"
    ));

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (random.nextDouble() > TREE_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = world.getHighestBlockYAt(x, z) - 1;

        Location loc = new Location(world, x, y, z);

        if (!world.getBiome(loc).equals(DreamBiome.SCULK_PLAINS.getBiome())) return;

        try {
            StructureUtils.placeStructure(StructureUtils.getStructureFile("omc_dream", TREE_FEATURES.get(random.nextInt(TREE_FEATURES.size()))), loc, false, false);
            generateSoilUnder(loc, 10, 7);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateSoilUnder(Location base, int radius, int depth) {
        World world = base.getWorld();
        int x = base.getBlockX();
        int z = base.getBlockZ();
        int y = base.getBlockY();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= radius) {
                    for (int dy = 0; dy > -depth; dy--) {
                        Block b = world.getBlockAt(x + dx, y + dy, z + dz);

                        if (!b.getType().isAir() && b.getType().isSolid()) break;

                        b.setType(PLAINS_SURFACE_MATERIAL);
                    }
                }
            }
        }
    }
}