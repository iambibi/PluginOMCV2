package fr.openmc.core.features.dream.listeners.orb;

import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.models.DBDreamPlayer;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;

public class PlayerObtainOrb implements Listener {
    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;

        if (event.getViewers().isEmpty()) return;
        Player player = (Player) event.getViewers().getFirst();

        if (!player.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        if (recipe instanceof Keyed keyed) {
            NamespacedKey key = keyed.getKey();
            if (key.toString().contains("omc_dream") && key.toString().contains("domination_orb")) { // contains beacuse key is ex zzzfake_omc_items:aywenite
                DBDreamPlayer cache = DreamManager.getCacheDreamPlayer(player);
                if (cache != null && cache.getProgressionOrb() < 1) {
                    cache.setProgressionOrb(1);
                    DreamManager.saveDreamPlayerData(cache);
                }
            }
        }
    }
}
