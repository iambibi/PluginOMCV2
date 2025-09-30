package fr.openmc.core.features.dream.generation.structures.soulforest;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.utils.SchematicsUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Random;

public class SoulAltarStructure extends BlockPopulator {
    private static final double SOUL_ALTAR_PROBABILITY = 0.001;

    public static final String schemSoulAltarName = "soul_altar";
    public static final File soulAltarFile = new File(OMCPlugin.getInstance().getDataFolder() + "/schem", schemSoulAltarName + ".schem");

    @Override
    public void populate(@NotNull World world, @NotNull Random random, @NotNull Chunk chunk) {
        if (random.nextDouble() >= SOUL_ALTAR_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = world.getHighestBlockYAt(x, z);

        Location loc = new Location(world, x, y, z);

        if (!world.getBiome(loc).equals(DreamBiome.SOUL_FOREST.getBiome())) return;

        System.out.println("SOUL ALTAR TROUVE x=" + x + " y=" + y + " z=" + z);

        SchematicsUtils.pasteSchem(world, soulAltarFile, loc);
    }
}