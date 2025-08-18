package fr.openmc.core.features.dream.generation.populators.forest;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator.CAVE_MATERIALS;
import static fr.openmc.core.features.dream.generation.biomes.SoulForestChunkGenerator.FOREST_SURFACE_MATERIAL;

public class PillarPopulator extends BlockPopulator {
    private static final double PILLAR_PROBABILITY = 0.5;

    private static final double BASE_RADIUS = 4.0;
    private static final int ADDITIVE_BASE_RADIUS = 3;
    private static final double TOP_RADIUS = 1.2;


    private static final int MIN_DISTANCE = 26;
    private final List<Location> pillars = new ArrayList<>();

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if (random.nextDouble() > PILLAR_PROBABILITY) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);
        int y = world.getHighestBlockYAt(x, z);

        Location testLoc = new Location(world, x, y, z);

        if (!world.getBiome(testLoc).equals(Biome.FOREST)) return;

        for (Location loc : pillars) {
            if (loc.getWorld().equals(world) && loc.distance(testLoc) < MIN_DISTANCE) {
                return;
            }
        }

        generatePillar(
                world, random, x, y, z,
                20 + random.nextInt(35),
                CAVE_MATERIALS,
                FOREST_SURFACE_MATERIAL
        );

        pillars.add(testLoc);
    }

    //note: generated with IA for the randomly pillars
    public static void generatePillar(World world, Random random,
                                      int baseX, int baseY, int baseZ,
                                      int height,
                                      List<Material> bodyMat,
                                      Material surfaceMat) {
        for (int yy = 0; yy < height; yy++) {
            double t = yy / (double) height;

            double r = lerp(BASE_RADIUS + random.nextInt(ADDITIVE_BASE_RADIUS), TOP_RADIUS, t)
                    + Math.sin(yy * 0.17) * 0.7
                    + (random.nextDouble() - 0.5) * 0.4;
            if (r < 1.0) r = 1.0;

            int offX = (int) Math.round(Math.sin(yy * 0.09) * 2.0);
            int offZ = (int) Math.round(Math.cos(yy * 0.11) * 2.0);

            int cx = baseX + offX;
            int cz = baseZ + offZ;
            int y = baseY + yy;

            int bound = (int) Math.ceil(r) + 1;
            for (int dx = -bound; dx <= bound; dx++) {
                for (int dz = -bound; dz <= bound; dz++) {
                    double edge = r + (random.nextDouble() - 0.5) * 0.6;
                    if (dx * dx + dz * dz <= edge * edge) {
                        world.getBlockAt(cx + dx, y, cz + dz).setType(bodyMat.get(random.nextInt(bodyMat.size())), false);
                    }
                }
            }

            if (yy > 6 && yy < height - 4 && random.nextDouble() < 0.10) {
                addShelf(world, random, cx, y, cz, r, bodyMat, surfaceMat);
            }
        }

        int topY = baseY + height;
        int topX = baseX + (int) Math.round(Math.sin(height * 0.09) * 2.0);
        int topZ = baseZ + (int) Math.round(Math.cos(height * 0.11) * 2.0);
        addDisc(world, topX, topY, topZ, 2 + random.nextInt(2), surfaceMat);
    }

    private static void addShelf(World world, Random random,
                                 int pillarX, int y, int pillarZ,
                                 double pillarRadius,
                                 List<Material> bodyMat, Material surfaceMat) {

        double angle = random.nextDouble() * Math.PI * 2;
        double ux = Math.cos(angle), uz = Math.sin(angle);

        int shelfRadius = 2 + random.nextInt(3);
        int attachX = pillarX + (int) Math.round(ux * Math.ceil(pillarRadius));
        int attachZ = pillarZ + (int) Math.round(uz * Math.ceil(pillarRadius));

        int cx = pillarX + (int) Math.round(ux * (pillarRadius + shelfRadius + 1));
        int cz = pillarZ + (int) Math.round(uz * (pillarRadius + shelfRadius + 1));

        drawLine(random, world, attachX, y, attachZ, cx, y, cz, bodyMat);

        addDisc(world, cx, y, cz, shelfRadius, surfaceMat);
    }

    private static void addDisc(World world, int cx, int y, int cz, int radius, Material mat) {
        int r2 = radius * radius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= r2) {
                    world.getBlockAt(cx + dx, y, cz + dz).setType(mat, false);
                }
            }
        }
    }

    private static void drawLine(Random random, World world, int x0, int y, int z0, int x1, int y1, int z1, List<Material> mat) {
        int dx = Math.abs(x1 - x0), dz = Math.abs(z1 - z0);
        int sx = x0 < x1 ? 1 : -1;
        int sz = z0 < z1 ? 1 : -1;
        int err = dx - dz;
        int x = x0, z = z0;
        while (true) {
            world.getBlockAt(x, y, z).setType(mat.get(random.nextInt(mat.size())), false);
            if (x == x1 && z == z1) break;
            int e2 = 2 * err;
            if (e2 > -dz) {
                err -= dz;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                z += sz;
            }
        }
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}