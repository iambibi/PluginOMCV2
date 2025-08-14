package fr.openmc.core.features.graves;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.api.WorldGuardApi;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.PlayerInventory;

public class GraveListener implements org.bukkit.event.Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        PlayerInventory inv = e.getEntity().getInventory();

        Location deathLocation = player.getLocation().clone();

        Block blockBelow = deathLocation.getBlock();
        Material typeBelow = blockBelow.getType();
        if (isHalfBlock(typeBelow)) {
            deathLocation.add(0, 1, 0);
        }

        boolean canSpawnGrave = true;
        Location baseLoc = deathLocation.clone();

        for (int x = -7; x <= 7 && canSpawnGrave; x++) {
            for (int y = -7; y <= 7 && canSpawnGrave; y++) {
                for (int z = -7; z <= 7 && canSpawnGrave; z++) {
                    Block block = baseLoc.clone().add(x, y, z).getBlock();
                    if (block.getType() == Material.WATER) {
                        canSpawnGrave = false;
                    }
                }
            }
        }

        if (canSpawnGrave) {
            City cityChunk = CityManager.getCityFromChunk(
                    deathLocation.getChunk().getX(),
                    deathLocation.getChunk().getZ()
            );

            if (cityChunk != null) {
                canSpawnGrave = false;
            }
        }

        if (canSpawnGrave) {
            if (WorldGuardApi.isRegionConflict(deathLocation)) {
                canSpawnGrave = false;
            }
        }

        e.getDrops().clear();

        if (!canSpawnGrave) {
            MessagesManager.sendMessage(player, Component.text("§8§oVous êtes mort dans un endroit ou dans une situation où la tombe ne peut pas être posé. Vous avez donc votre stuff dans votre inventaire"), Prefix.OPENMC, MessageType.INFO, false);
            e.setKeepInventory(true);
        } else {
            GraveManager.addCorpse(player, inv, deathLocation);
        }
    }

    private boolean isHalfBlock(Material type) {
        return switch (type) {
            case DIRT_PATH, OAK_SLAB, SPRUCE_SLAB, BIRCH_SLAB, JUNGLE_SLAB, ACACIA_SLAB, DARK_OAK_SLAB, STONE_SLAB,
                 SANDSTONE_SLAB, COBBLESTONE_SLAB, BRICK_SLAB, STONE_BRICK_SLAB, NETHER_BRICK_SLAB, QUARTZ_SLAB,
                 RED_SANDSTONE_SLAB, PURPUR_SLAB, PRISMARINE_SLAB, PRISMARINE_BRICK_SLAB, DARK_PRISMARINE_SLAB -> true;
            default -> false;
        };
    }


    @EventHandler
    public void onGraveInteract(CustomBlockInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getNamespacedID().equals("omc_blocks:grave")) {
            GraveManager.open(e.getPlayer());
        }
    }

    @EventHandler
    public void onGraveBreak(CustomBlockBreakEvent e) {
        String namespace = e.getNamespacedID();

        if (namespace.equals("omc_blocks:grave")) {
            e.setCancelled(true);
        }
    }
}