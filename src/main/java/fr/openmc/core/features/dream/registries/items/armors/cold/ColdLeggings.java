package fr.openmc.core.features.dream.registries.items.armors.cold;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ColdLeggings extends DreamItem {
    public ColdLeggings(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.LEGENDARY;
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
        ItemStack item = new ItemStack(Material.DIAMOND_LEGGINGS);

        item.getItemMeta().displayName(Component.text("Jambière Glacée"));
        return item;
    }
}
