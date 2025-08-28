package fr.openmc.core.features.dream.models;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.displays.DreamBossBar;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

@Getter
@Setter
public class DreamStats {

    private final Player player;
    private Long dreamTime;
    private final Long maxDreamTime;
    private final BukkitTask task;

    public DreamStats(Player player) {
        this.player = player;

        this.maxDreamTime = DreamManager.calculateMaxDreamTime();
        this.dreamTime = maxDreamTime;

        this.task = scheduleTask();
    }

    public void addTime(Long additionalTime) {
        this.dreamTime += additionalTime;
    }

    public void cancelTask() {
        if (task != null) task.cancel();
    }

    public BukkitTask scheduleTask() {
        return Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            this.dreamTime -= 1;

            if (dreamTime <= 0) {
                this.cancelTask();
                return;
            }

            DreamBossBar.update(player, (float) this.getDreamTime() / this.getMaxDreamTime());
        }, 0L, 20L);
    }
}
