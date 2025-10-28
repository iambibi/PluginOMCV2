package fr.openmc.core.features.dream.enchantements;

import fr.openmc.core.OMCPlugin;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@SuppressWarnings("UnstableApiUsage")
public class DreamEnchantementRegistry {

    private final static HashMap<String, Enchantment> dreamEnchantment = new HashMap<>();

    public static void loadInBootstrap(RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.@NotNull Builder> event) {
        event.registry().register(
                EnchantmentKeys.create(Key.key("dream:dream_sleeper")),
                b -> b.description(Component.text("Endormant"))
                        .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.SWORDS))
                        .anvilCost(2)
                        .maxLevel(3)
                        .weight(1)
                        .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                        .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 4))
                        .activeSlots(EquipmentSlotGroup.ANY)
        );
    }

    public static void init() {
        OMCPlugin.registerEvents(
                new DreamSleeper()
        );

        Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess
                .registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT);

        registerEnchantment("dream:dream_sleeper", enchantmentRegistry.getOrThrow(
                RegistryKey.ENCHANTMENT.typedKey(Key.key("dream:dream_sleeper"))
        ));
    }

    public static void registerEnchantment(String key, Enchantment enchantment) {
        dreamEnchantment.put(key, enchantment);
    }

    public static Enchantment getDreamEnchantment(String key) {
        return dreamEnchantment.get(key);
    }
}
