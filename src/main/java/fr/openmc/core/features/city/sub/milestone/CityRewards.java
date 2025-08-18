package fr.openmc.core.features.city.sub.milestone;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public interface CityRewards {
    ItemStack getIcon();

    Component getName();

    Component getDescription();

    Runnable getMethods();
}
