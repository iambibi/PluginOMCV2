package fr.openmc.core.features.dream.registries.items.tools;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.features.dream.models.registry.items.DreamUsableItem;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MeteoWand extends DreamUsableItem {
    public MeteoWand(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.LEGENDARY;
    }

    @Override
    public boolean isTransferable() {
        return true;
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getItemsAdder();
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.STICK);

        item.getItemMeta().displayName(Component.text("Meteo Wand"));
        return item;
    }

    @Override
    public void onRightClick(Player player, PlayerInteractEvent event) {
        World world = player.getWorld();
        if (!world.getName().equals("world")) {
            MessagesManager.sendMessage(player, Component.text("Vous devez être dans l'overworld pour utiliser cet objet"), Prefix.OPENMC, MessageType.WARNING, false);
            return;
        }

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 12) {
                    cancel();
                    return;
                }

                long newTime = (world.getTime() + 1000L) % 24000L;
                world.setTime(newTime);
                world.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 10f, 0.3f);

                count++;
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 40L);
    }
}
