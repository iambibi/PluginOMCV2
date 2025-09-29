package fr.openmc.api.hooks;

import org.bukkit.Bukkit;

public class FancyNpcsHook {
    private static boolean hasFancyNpc = false;

    public FancyNpcsHook() {
        hasFancyNpc = Bukkit.getPluginManager().isPluginEnabled("FancyNpcs");
    }

    /**
     * Retourne si l'instance a FancyNpc
     */
    public static boolean hasFancyNpc() {
        return hasFancyNpc;
    }
}