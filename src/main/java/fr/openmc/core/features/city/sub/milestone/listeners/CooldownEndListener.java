package fr.openmc.core.features.city.sub.milestone.listeners;

import fr.openmc.api.cooldown.CooldownEndEvent;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class CooldownEndListener implements Listener {
    @EventHandler
    public void onEnd(CooldownEndEvent event) {
        String group = event.getGroup();

        if (!Objects.equals(group, "city:upgrade-level")) return;

        City city = CityManager.getCity(event.getUUID());

        int oldLevel = city.getLevel();

        if (oldLevel + 1 > CityLevels.values().length) return;

        city.setLevel(oldLevel + 1);

        CityStatisticsManager.removeStats(city.getUUID());
    }
}
