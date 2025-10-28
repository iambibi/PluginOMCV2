package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.List;

public class PlayerCommandListener implements Listener {
    @EventHandler
    public void onCommandExecution(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!player.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        // todo: activer que les commandes dont le joueur a besoin
        if (player.isOp()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onCommandAutocomplete(TabCompleteEvent event) {
        Location loc = event.getLocation();

        if (loc == null) return;

        if (!loc.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        if (event.getSender() instanceof Player player && player.isOp()) return;

        event.setCompletions(List.of("milestone dream"));
    }
}
