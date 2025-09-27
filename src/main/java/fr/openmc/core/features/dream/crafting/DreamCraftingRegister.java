package fr.openmc.core.features.dream.crafting;

import fr.openmc.core.OMCPlugin;

public class DreamCraftingRegister {
    public DreamCraftingRegister() {
        OMCPlugin.registerEvents(
                new DreamCraftingListener()
        );
    }
}
