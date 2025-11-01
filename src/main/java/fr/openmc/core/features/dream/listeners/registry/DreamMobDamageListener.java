package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DreamMobDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (!DreamMobsRegistry.isDreamMob(livingEntity)) return;

        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            Entity damager = entityEvent.getDamager();

            if (damager instanceof Player) return;
        }

        event.setCancelled(true);
    }

}
