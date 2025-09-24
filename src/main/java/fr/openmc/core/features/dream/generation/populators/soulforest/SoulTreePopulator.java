package fr.openmc.core.features.dream.generation.populators.soulforest;

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


public class SoulTreePopulator extends BlockPopulator {
    private static final double TREE_PROBABILITY = 0.50;
    private static final List<String> TREE_FEATURES = new ArrayList<>(List.of(
            "soul_forest/tree_1",
            "soul_forest/tree_2",
            "soul_forest/tree_3",
            "soul_forest/tree_4"
    ));

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (random.nextDouble() >= TREE_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = world.getHighestBlockYAt(x, z);

        Location loc = new Location(world, x, y, z);

        if (!world.getBiome(loc).equals(DreamBiome.SOUL_FOREST.getBiome())) return;

        try {
            StructureUtils.placeStructure(
                    StructureUtils.getStructureNBT(
                            "omc_dream",
                            TREE_FEATURES.get(random.nextInt(TREE_FEATURES.size()))
                    ),
                    loc,
                    false,
                    false,
                    false
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}