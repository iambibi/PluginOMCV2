package fr.openmc.core.features.dream.enchantements;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DreamSleeper implements Listener {
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        if (!DynamicCooldownManager.isReady(player.getUniqueId(), "dream:dream_sleeper")) return;

        Entity entityAttacked = event.getEntity();
        if (entityAttacked instanceof Player) return;

        if (entityAttacked instanceof LivingEntity livingEntity) {
            ItemStack item = player.getInventory().getItemInMainHand();

            Enchantment enchantment = DreamEnchantementRegistry.getDreamEnchantment("dream:dream_sleeper");
            if (!item.getEnchantments().containsKey(enchantment)) return;

            livingEntity.setAI(false);
            DynamicCooldownManager.use(player.getUniqueId(), "dream:dream_sleeper", 5 * 20L);

            int level = item.getEnchantments().get(enchantment);

            new BukkitRunnable() {
                @Override
                public void run() {
                    livingEntity.setAI(true);
                }
            }.runTaskLater(OMCPlugin.getInstance(), 15L * level);
        }
    }
}
