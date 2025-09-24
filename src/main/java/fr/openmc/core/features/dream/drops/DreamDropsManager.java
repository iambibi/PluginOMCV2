package fr.openmc.core.features.dream.drops;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.items.DreamItemRegister;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DreamDropsManager {

    private final static HashMap<Material, ItemStack> customDrops = new HashMap<>();

    public DreamDropsManager() {
        OMCPlugin.registerEvents(
                new DreamDropsListener()
        );

        registerCustomDrop(Material.SCULK, DreamItemRegister.getByName("corrupted_sculk").getBest());
    }

    public void registerCustomDrop(Material mat, ItemStack item) {
        customDrops.put(mat, item);
    }

    public static ItemStack getCustomDrop(Material mat) {
        return customDrops.get(mat);
    }
}
