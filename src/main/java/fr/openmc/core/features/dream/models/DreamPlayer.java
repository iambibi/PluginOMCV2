package fr.openmc.core.features.dream.models;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.displays.DreamBossBar;
import fr.openmc.core.utils.serializer.BukkitSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

@Getter
public class DreamPlayer {
    private final Player player;
    @Setter
    private OldInventory oldInventory;
    @Setter
    private PlayerInventory dreamInventory;

    private Long dreamTime;
    private Long maxDreamTime;
    private final BukkitTask task;

    public DreamPlayer(Player player, OldInventory oldInv, PlayerInventory dreamInv) {
        this.player = player;
        this.oldInventory = oldInv;
        this.dreamInventory = dreamInv;

        DBDreamPlayer cacheData = DreamManager.getCacheDreamPlayer(player);

        this.maxDreamTime = cacheData == null ? DreamManager.BASE_DREAM_TIME : cacheData.getMaxDreamTime();
        this.dreamTime = maxDreamTime;

        this.task = this.scheduleTask();
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }

    public Location getLocation() {
        return player.getLocation();
    }

    public void addTime(Long additionalTime) {
        this.dreamTime += additionalTime;
    }

    public void removeTime(Long removedTime) {
        if (dreamTime - removedTime <= 0L) {
            this.dreamTime = 0L;
            return;
        }

        this.dreamTime -= removedTime;
    }

    public void addMaxTime(Long additionalTime) {
        this.maxDreamTime += additionalTime;
    }

    public void removeMaxTime(Long removedTime) {
        if (maxDreamTime - removedTime <= 0L) {
            this.maxDreamTime = 0L;
            return;
        }

        this.maxDreamTime -= removedTime;
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

    public DBDreamPlayer serialize() {
        return new DBDreamPlayer(this.player.getUniqueId(), this.maxDreamTime, BukkitSerializer.playerInventoryToBase64(this.dreamInventory));
    }
}
