package fr.openmc.core.features.dream.mobs.listeners;

import fr.openmc.core.features.dream.mobs.DreamLoot;
import fr.openmc.core.features.dream.mobs.DreamMob;
import fr.openmc.core.features.dream.mobs.DreamMobManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DreamMobLootListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!DreamMobManager.isDreamMob(entity)) return;

        DreamMob dreamMob = DreamMobManager.getFromEntity(entity);
        if (dreamMob == null) return;

        event.getDrops().clear();
        event.setDroppedExp(0);

        for (DreamLoot loot : dreamMob.getDreamLoots()) {
            if (Math.random() < loot.getChance()) {
                int amount = loot.getMinAmount() + (int) (Math.random() * (loot.getMaxAmount() - loot.getMinAmount() + 1));
                ItemStack drop = loot.getItem().getBest().asQuantity(amount);
                entity.getWorld().dropItemNaturally(entity.getLocation(), drop);
            }
        }
    }
}
