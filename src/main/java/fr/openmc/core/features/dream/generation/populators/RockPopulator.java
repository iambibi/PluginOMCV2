package fr.openmc.core.features.dream.generation.populators;

import fr.openmc.core.utils.StructureUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RockPopulator extends BlockPopulator {
    private static final double ROCK_PROBABILITY = 0.5;
    private static final List<String> ROCK_FEATURES = new ArrayList<>(List.of(
            "rock_1",
            "rock_2",
            "rock_3",
            "rock_4",
            "rock_5",
            "rock_6",
            "rock_7"
    ));

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (random.nextDouble() > ROCK_PROBABILITY) return;

        // Position aléatoire dans le chunk
        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = world.getHighestBlockYAt(x, z); // sol

        Location loc = new Location(world, x, y, z);

        if (!world.getBiome(loc).equals(Biome.BEACH)) return;

        try {
            StructureUtils.placeStructure(StructureUtils.getStructureFile("omc_dream", ROCK_FEATURES.get(random.nextInt(ROCK_FEATURES.size()))), loc, random.nextBoolean(), random.nextBoolean());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}