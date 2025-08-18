package fr.openmc.core.features.dream.generation.effects;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.utils.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BiomeParticleManager {

    private static final Map<UUID, BukkitTask> runningTasks = new HashMap<>();

    // ** PARTICLES FOR EACH ZONES **
    private static final Map<Biome, Particle> biomeParticles = new HashMap<>() {
        {
            put(Biome.FOREST, Particle.SCULK_SOUL);
            put(Biome.PLAINS, Particle.TRIAL_SPAWNER_DETECTION_OMINOUS);
            put(Biome.BEACH, Particle.ASH);
            put(Biome.DEEP_DARK, Particle.SNOWFLAKE);
            put(Biome.THE_VOID, Particle.SMALL_GUST);
        }
    };
    private static final Particle cloudParticles = Particle.SMALL_GUST;
    private static final Particle glaciteParticles = Particle.SNOWFLAKE;

    // ** CONST **
    private static final int PARTICLE_RADIUS = 8;

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

            Biome biome = player.getLocation().getBlock().getBiome();

            if (biomeParticles.containsKey(biome)) {
                ParticleUtils.sendRandomCubeParticles(player, biomeParticles.get(biome), PARTICLE_RADIUS, 50);
                return;
            }

            stopTask(player);
        }, 0L, 20L);

        runningTasks.put(player.getUniqueId(), task);
    }

    public static void stopTask(Player player) {
        BukkitTask task = runningTasks.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }
}
