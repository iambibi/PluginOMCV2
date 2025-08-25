package fr.openmc.core.features.dream;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.listeners.PlayerChangeWorldListener;
import fr.openmc.core.features.dream.listeners.PlayerQuitListener;
import fr.openmc.core.features.dream.models.DreamStats;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DreamManager {

    private static final HashMap<Player, DreamStats> playerData = new HashMap<>();

    public DreamManager() {

        OMCPlugin.registerEvents(
                new PlayerChangeWorldListener(),
                new PlayerQuitListener()
        );

        new DreamDimensionManager();
    }

    public static DreamStats getDreamStats(Player player) {
        if (!playerData.containsKey(player)) return null;

        return playerData.get(player);
    }

    public static void addPlayer(Player player) {
        playerData.put(player, new DreamStats(player));
    }

    public static void removePlayer(Player player) {
        playerData.remove(player);
    }

    public static long calculateMaxDreamTime() {
        long base = 300;

        return base;
    }
}
