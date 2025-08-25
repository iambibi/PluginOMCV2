package fr.openmc.core.features.dream.listeners;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.models.DreamStats;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuitWhenDream(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        World world = player.getLocation().getWorld();

        if (!world.getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        DreamStats stats = DreamManager.getDreamStats(player);

        if (stats == null) {
            DreamManager.removePlayer(player);
            return;
        }

        stats.cancelTask();

        DreamManager.removePlayer(player);
    }
}
