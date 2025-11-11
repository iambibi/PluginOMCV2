package fr.openmc.core.features.dream.registries.items.blocks;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PoweredFurnace extends DreamItem {
    public PoweredFurnace(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.EPIC;
    }

    @Override
    public boolean isTransferable() {
        return false;
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.FURNACE);

        item.getItemMeta().displayName(Component.text("Four Amélioré"));
        return item;
    }
}
