package fr.openmc.core.features.city.sub.milestone;

import fr.openmc.core.features.city.sub.milestone.requirements.CommandRequirement;
import fr.openmc.core.features.city.sub.milestone.requirements.ItemDepositRequirement;
import fr.openmc.core.features.city.sub.milestone.requirements.TemplateRequirement;
import fr.openmc.core.items.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public enum CityLevels {
    LEVEL_1(
            Component.text("Niveau 1"),
            Component.text("Ere Urbaine"),
            List.of(
                    new CommandRequirement("/city create", 1)
            ),
            0
    ),
    LEVEL_2(
            Component.text("Niveau 2"),
            Component.text("Les Fondations"),
            List.of(
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 5,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            city -> Component.text("Avoir 5 Chunks (" + city.getChunks().size() + "/5)")
                    ),
                    new TemplateRequirement(
                            city -> city.getLaw().getWarp() != null,
                            city -> CustomItemRegistry.getByName("omc_items:warp_stick").getBest(),
                            city -> Component.text("Poser un /city warp")
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 2,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            city -> Component.text("Avoir sa Mascotte Niveau 2")
                    ),
                    new CommandRequirement("/city create", 1),
                    new ItemDepositRequirement(ItemStack.of(Material.GOLD_INGOT), 128)
            ),
            60 * 5
    ),

    ;

    private final Component name;
    private final Component description;
    private final List<CityRequirement> requirements;
    private final int upgradeTime;

    CityLevels(Component name, Component description, List<CityRequirement> requirements, int upgradeTime) {
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        this.upgradeTime = upgradeTime;
    }
}
