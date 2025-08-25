package fr.openmc.core.features.dream.generation.structures.cloud;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.biomes.CloudChunkGenerator;
import fr.openmc.core.utils.SchematicsUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Random;

public class CloudCastleStructure extends BlockPopulator {
    private static final double CLOUD_CASTLE_PROBABILITY = 0.0007;

    public static final String schemCloudCastleName = "cloud_castle";

    public static final File cloudCastleFile = new File(OMCPlugin.getInstance().getDataFolder() + "/schem", schemCloudCastleName + ".schem");

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (random.nextDouble() > CLOUD_CASTLE_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = CloudChunkGenerator.MAX_HEIGHT_CLOUD;

        Location loc = new Location(world, x, y, z);

        if (world.getBiome(loc).equals(DreamBiome.SOUL_FOREST.getBiome())) return;

        System.out.println("structyreurueuru e");
        SchematicsUtils.pasteSchem(world, cloudCastleFile, loc.add(0, 75, 0));
    }
}