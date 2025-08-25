package fr.openmc.core.features.dream;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.listeners.PlayerChangeWorldListener;

public class DreamManager {
    public DreamManager() {

        OMCPlugin.registerEvents(
                new PlayerChangeWorldListener()
        );

        new DreamDimensionManager();
    }
}
