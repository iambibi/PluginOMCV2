package fr.openmc.core.features.dream.generation.structures.glacite;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator;
import fr.openmc.core.utils.SchematicsUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Random;

public class BaseCampStructure extends BlockPopulator {
    private static final double BASE_CAMP_PROBABILITY = 0.0001;

    public static final String schemBaseCampName = "base_camp";
    public static final File baseCampFile = new File(OMCPlugin.getInstance().getDataFolder() + "/schem", schemBaseCampName + ".schem");

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (random.nextDouble() >= BASE_CAMP_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = GlaciteCaveChunkGenerator.MIN_CAVE_HEIGHT + 1;

        Location loc = new Location(world, x, y, z);

        System.out.println("BASE CAMP TROUVE x=" + x + " y=" + y + " z=" + z);

        SchematicsUtils.pasteSchem(world, baseCampFile, loc.add(0, 57, 0));
    }
}