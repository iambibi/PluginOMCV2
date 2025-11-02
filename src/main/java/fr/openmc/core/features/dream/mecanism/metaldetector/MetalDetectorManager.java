package fr.openmc.core.features.dream.mecanism.metaldetector;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.models.registry.DreamLoot;
import fr.openmc.core.features.dream.registries.DreamEnchantementRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MetalDetectorManager {
    public static final Map<UUID, MetalDetectorTask> hiddenChests = new HashMap<>();

    private static final Set<DreamLoot> METAL_DETECTOR_LOOTS = Set.of(
            new DreamLoot(
                    DreamItemRegistry.getByName("omc_dream:somnifere"),
                    0.4,
                    1,
                    1
            ),

            new DreamLoot(
                    DreamEnchantementRegistry.getDreamEnchantment(Key.key("dream:experientastic")).getEnchantedBookItem(1),
                    0.1,
                    1,
                    2
            ),

            new DreamLoot(
                    DreamItemRegistry.getByName("omc_dream:mud_orb"),
                    0.2,
                    1,
                    1
            )
    );

    public static void init() {
        OMCPlugin.registerEvents(
                new MetalDetectorListener()
        );
    }

    public static List<ItemStack> rollMetalDetectorLoots() {
        Random random = new Random();
        List<ItemStack> loot = new ArrayList<>();

        int luck = random.nextInt(100);

        if (luck < 50) {
            List<ItemStack> rolls = List.of(
                    DreamItemRegistry.getByName("omc_dream:cloud_helmet").getBest(),
                    DreamItemRegistry.getByName("omc_dream:cloud_chestplate").getBest(),
                    DreamItemRegistry.getByName("omc_dream:cloud_leggings").getBest(),
                    DreamItemRegistry.getByName("omc_dream:cloud_boots").getBest()
            );

            loot.add(rolls.get(random.nextInt(rolls.size())));
        } else if (luck < 75) {
            loot.add(DreamItemRegistry.getByName("omc_dream:somnifere").getBest());
        } else if (luck < 90) {
            loot.add(DreamItemRegistry.getByName("omc_dream:cloud_fishing_rod").getBest());
        } else {
            ItemStack bookEnchanted = DreamEnchantementRegistry.getDreamEnchantment(
                    Key.key("dream:dream_sleeper")
            ).getEnchantedBookItem(2).getBest();
            loot.add(bookEnchanted);
        }
        return loot;
    }
}
