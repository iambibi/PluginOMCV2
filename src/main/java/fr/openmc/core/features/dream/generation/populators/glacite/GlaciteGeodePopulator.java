package fr.openmc.core.features.dream.generation.populators.glacite;

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

import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.MAX_CAVE_HEIGHT;
import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.MIN_CAVE_HEIGHT;


public class GlaciteGeodePopulator extends BlockPopulator {
    private static final double CHUNK_GEODE_PROBABILITY = 0.1;

    private static final List<String> GEODE_FEATURES = new ArrayList<>(List.of(
            "glacite/geode"
    ));

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (random.nextDouble() >= CHUNK_GEODE_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);

        int y = MIN_CAVE_HEIGHT + random.nextInt(MAX_CAVE_HEIGHT - MIN_CAVE_HEIGHT);

        Location loc = new Location(world, x, y, z);

        if (!world.getBiome(loc).equals(DreamBiome.GLACITE_GROTTO.getBiome())) return;

        try {
            StructureUtils.placeStructure(
                    StructureUtils.getStructureFile("omc_dream",
                            GEODE_FEATURES.get(random.nextInt(GEODE_FEATURES.size()))),
                    loc,
                    random.nextBoolean(),
                    random.nextBoolean()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
