package fr.openmc.core.features.dream.items;

import fr.openmc.core.features.dream.items.blocks.CorruptedSculk;
import fr.openmc.core.features.dream.items.orb.*;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.HashMap;

public class DreamItemRegister {
    static final HashMap<String, DreamItem> dreamItems = new HashMap<>();
    static final NamespacedKey customNameKey = new NamespacedKey("aywen", "dream_item");

    public DreamItemRegister() {
        // # ORBES
        registerDreamItem(new DominationOrb("omc_dream:domination_orb"));
        registerDreamItem(new SoulOrb("omc_dream:ame_orb"));
        registerDreamItem(new MudOrb("omc_dream:mud_orb"));
        registerDreamItem(new CloudOrb("omc_dream:cloud_orb"));
        registerDreamItem(new GlaciteOrb("omc_dream:glacite_orb"));

        // # BLOCS
        System.out.println("register " + new CorruptedSculk("corrupted_sculk").getBest());
        registerDreamItem(new CorruptedSculk("corrupted_sculk"));
    }

    public static void register(String name, DreamItem item) {
        if (!name.matches("[a-zA-Z0-9_:]+")) {
            throw new IllegalArgumentException("Custom item name dont match regex \"[a-zA-Z0-9_:]+\"");
        }

        dreamItems.put(name, item);
    }

    public static void registerDreamItem(DreamItem item) {
        register(item.getName(), item);
    }

    @Nullable
    public static DreamItem getByName(String name) {
        return dreamItems.get(name);
    }

    @Nullable
    public static DreamItem getByItemStack(ItemStack stack) {
        PersistentDataContainerView view = stack.getPersistentDataContainer();
        String name = view.get(customNameKey, PersistentDataType.STRING);

        return name == null ? null : getByName(name);
    }
}
