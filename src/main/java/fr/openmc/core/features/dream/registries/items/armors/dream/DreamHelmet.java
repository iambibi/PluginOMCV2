package fr.openmc.core.features.dream.registries.items.armors.dream;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DreamHelmet extends DreamItem {
    public DreamHelmet(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.ONIRISIME;
    }

    @Override
    public boolean isTransferable() {
        return true;
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.NETHERITE_HELMET);

        item.getItemMeta().displayName(Component.text("Casque Onirique"));
        return item;
    }
}
