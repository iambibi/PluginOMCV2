package fr.openmc.core.features.dream.listeners;

import fr.openmc.core.features.displays.scoreboards.ScoreboardManager;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangeWorldListener implements Listener {

    @EventHandler
    public void onDreamEntrered(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getLocation().getWorld();

        if (!world.getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        ScoreboardManager.removePlayerScoreboard(player);
        ScoreboardManager.createNewScoreboard(player);
    }
}
