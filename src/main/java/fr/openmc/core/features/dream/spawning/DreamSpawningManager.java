package fr.openmc.core.features.dream.spawning;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.spawning.listeners.PlainsMobSpawningListener;
import org.bukkit.NamespacedKey;

/**
 * Gestionnaire de l'apparition des mobs dans la Dimension des Rêves.
 *
 * <p>Cette classe initialise les probabilités d'apparition des mobs ainsi que
 * l'enregistrement des listeners correspondants.</p>
 */
public class DreamSpawningManager {

    public static final NamespacedKey mobKey = new NamespacedKey("openmc", "dream_mob");

    /**
     * Constructeur qui initialise la probabilité d'apparition et enregistre les événements.
     */
    public DreamSpawningManager() {
        OMCPlugin.registerEvents(
                new PlainsMobSpawningListener()
        );
    }
}
