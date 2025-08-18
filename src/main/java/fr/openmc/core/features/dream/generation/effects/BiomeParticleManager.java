package fr.openmc.core.features.dream.generation.effects;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.generation.biomes.CloudChunkGenerator;
import fr.openmc.core.features.dream.generation.biomes.GlaciteCaveChunkGenerator;
import fr.openmc.core.utils.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BiomeParticleManager {

    private static final Map<UUID, BukkitTask> runningTasks = new HashMap<>();
    private static final Map<Biome, Particle> biomeParticles = new HashMap<>() {
        {
            put(Biome.FOREST, Particle.SCULK_SOUL);
            put(Biome.PLAINS, Particle.TRIAL_SPAWNER_DETECTION_OMINOUS);
            put(Biome.BEACH, Particle.ASH);
        }
    };
    private static final Particle cloudParticles = Particle.SMALL_GUST;
    private static final Particle glaciteParticles = Particle.SNOWFLAKE;

    public static void startTask(Player player) {
        if (runningTasks.containsKey(player.getUniqueId())) return;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            if (!player.isOnline()) {
                stopTask(player);
                return;
            }

            if (!player.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) {
                stopTask(player);
                return;
            }

            Location location = player.getLocation();

            if (CloudChunkGenerator.MIN_HEIGHT_CLOUD < location.getY()) {
                ParticleUtils.sendCubeParticles(player, cloudParticles, 5, 0.5);
                return;
            }

            if (GlaciteCaveChunkGenerator.MAX_CAVE_HEIGHT > location.getY()) {
                ParticleUtils.sendCubeParticles(player, glaciteParticles, 5, 0.5);
                return;
            }

            Biome biome = player.getLocation().getBlock().getBiome();

            if (biomeParticles.containsKey(biome)) {
                ParticleUtils.sendCubeParticles(player, biomeParticles.get(biome), 5, 0.5);
                return;
            }

            stopTask(player);
            return;
        }, 0L, 20L);

        runningTasks.put(player.getUniqueId(), task);
    }

    public static void stopTask(Player player) {
        BukkitTask task = runningTasks.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }
}
