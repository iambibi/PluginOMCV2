package fr.openmc.core.features.dream.mecanism.cold;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class ColdListener implements Listener {
    @EventHandler
    public void onGlaciteGrottoEntered(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        if (loc.getBlock().getBiome().equals(DreamBiome.GLACITE_GROTTO.getBiome())) {
            DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
            if (dreamPlayer == null) return;

            if (dreamPlayer.getColdTask() == null) {
                System.out.println("scheduleTas");
                dreamPlayer.scheduleColdTask();
            }
        }
    }
}
