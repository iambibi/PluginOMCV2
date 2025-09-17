package fr.openmc.core.features.cube;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class CubeListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            if (!(mb instanceof Cube cube)) continue;

            Location clickedBlock = event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : null;
            if (cube.isPartOf(clickedBlock)) {
                cube.repulsePlayer(event.getPlayer(), false);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        for (MultiBlock mb : MultiBlockManager.getMultiBlocks()) {
            if (!(mb instanceof Cube cube)) continue;

            Location belowPlayer = player.getLocation().clone().subtract(0, 1, 0);
            if (cube.isPartOf(belowPlayer)) {
                cube.repulsePlayer(event.getPlayer(), true);
            }
        }
    }
}