package fr.openmc.core.features.graves;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.OMCPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraveManager {

    private static final List<Grave> graves = new ArrayList<>();

    public GraveManager() {
        OMCPlugin.registerEvents(
                new GraveListener()
        );
    }

    public static void addCorpse(Player p, PlayerInventory inv, Location deathLocation) {
        CustomBlock block = CustomBlock.getInstance("omc_blocks:grave");

        if (block != null) {
            block.place(deathLocation);

            Grave grave = new Grave(block, deathLocation, p.getUniqueId(), inv);
            graves.add(grave);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!graves.contains(grave)) return;

                    remove(grave);
                }
            }.runTaskLater(OMCPlugin.getInstance(), 20 * 60 * 10);
        }

    }

    public static void open(Player p) {
        for (Grave grave : graves) {
            GraveMenu graveMenu = grave.getGraveMenu();

            if (graveMenu != null && graveMenu.getOwner().getUniqueId().equals(p.getUniqueId())) {
                graveMenu.open();
            }
        }
    }

    public static void remove(Grave grave) {
        if (grave != null) {
            grave.remove();
        }
        graves.remove(grave);
    }

    public void removeAll() {
        Iterator<Grave> iterator = graves.iterator();
        while (iterator.hasNext()) {
            Grave grave = iterator.next();

            for (ItemStack it : grave.getGraveMenu().getInventory()) {
                if (it.getType() == Material.BLACK_STAINED_GLASS_PANE) continue;

                grave.getLocation().getWorld().dropItemNaturally(grave.getLocation(), it);
            }

            grave.remove();
            iterator.remove();
        }
    }

}
