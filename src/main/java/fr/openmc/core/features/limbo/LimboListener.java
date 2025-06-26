package fr.openmc.core.features.limbo;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static fr.openmc.core.features.limbo.LimboManager.blockedPlayers;
import static fr.openmc.core.features.limbo.LimboManager.isInLimbo;

public class LimboListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        LimboManager.limboPlayers.remove(event.getPlayer().getUniqueId());
        blockedPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (isInLimbo(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isInLimbo(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getUniqueId())) {
            if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (isInLimbo(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandInLimbo(PlayerCommandPreprocessEvent event) {
        ;
        Player player = event.getPlayer();
        if (!isInLimbo(player.getUniqueId())) return;

        String message = event.getMessage().toLowerCase();

        if (!message.equals("/limbo")) {
            event.setCancelled(true);
        }
    }
}
