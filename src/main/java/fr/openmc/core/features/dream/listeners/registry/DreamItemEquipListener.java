package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.events.ArmorEquipEvent;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DreamItemEquipListener implements Listener {

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        if (!DreamUtils.isInDream(player)) return;

        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);

        if (dreamPlayer == null) return;

        ItemStack oldPiece = event.getOldArmorPiece();
        ItemStack newPiece = event.getNewArmorPiece();

        DreamItem oldItem = DreamItemRegistry.getByItemStack(oldPiece);
        if (oldItem == null) return;

        if (oldItem instanceof DreamEquipableItem oldItemEquipable) {
            long time = oldItemEquipable.getAdditionalMaxTime();

            dreamPlayer.removeMaxTime(time);
        }

        DreamItem newItem = DreamItemRegistry.getByItemStack(newPiece);
        if (newItem == null) return;

        if (newItem instanceof DreamEquipableItem newItemEquipable) {
            long time = newItemEquipable.getAdditionalMaxTime();

            dreamPlayer.addMaxTime(time);
        }
    }

}