package fr.openmc.core.features.dream.generation.populators.plains;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.utils.StructureUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PlainsTreePopulator extends BlockPopulator {
    private static final double TREE_PROBABILITY = 0.15;
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
        int y = world.getHighestBlockYAt(x, z);

        Location loc = new Location(world, x, y, z);

        if (!world.getBiome(loc).equals(DreamBiome.SCULK_PLAINS.getBiome())) return;

        try {
            StructureUtils.placeStructure(StructureUtils.getStructureFile("omc_dream", TREE_FEATURES.get(random.nextInt(TREE_FEATURES.size()))), loc, false, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}