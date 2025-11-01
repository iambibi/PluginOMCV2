package fr.openmc.core.features.dream.mecanism.metaldetector;

import fr.openmc.core.OMCPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MetalDetectorManager {
    public static final Map<UUID, MetalDetectorTask> hiddenChests = new HashMap<>();

    public static void init() {
        OMCPlugin.registerEvents(
                new MetalDetectorListener()
        );
    }
}
