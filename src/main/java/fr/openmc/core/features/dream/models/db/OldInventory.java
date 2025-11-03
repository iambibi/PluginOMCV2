package fr.openmc.core.features.dream.models.db;

import fr.openmc.core.utils.serializer.BukkitSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record OldInventory(ItemStack[] oldContents, ItemStack[] oldArmor, ItemStack[] oldExtra) {

    public String getSerialized() {
        return BukkitSerializer.playerInventoryToBase64(oldContents, oldArmor, oldExtra);
    }

    public void restoreOldInventory(Player player) {
        player.getInventory().setContents(oldContents);
        player.getInventory().setArmorContents(oldArmor);
        player.getInventory().setExtraContents(oldExtra);
        player.updateInventory();
    }
}
