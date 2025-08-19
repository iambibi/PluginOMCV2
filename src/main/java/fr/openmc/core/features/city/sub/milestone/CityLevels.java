package fr.openmc.core.features.city.sub.milestone;

import fr.openmc.core.features.city.sub.milestone.requirements.CommandRequirement;
import fr.openmc.core.features.city.sub.milestone.requirements.ItemDepositRequirement;
import fr.openmc.core.features.city.sub.milestone.requirements.TemplateRequirement;
import fr.openmc.core.items.CustomItemRegistry;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
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
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal() + 1) {
                                    return Component.text("Avoir 5 Chunks");
                                }

                                return Component.text(String.format(
                                        "Avoir 5 Chunks (%d/5)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getLaw().getWarp() != null,
                            city -> CustomItemRegistry.getByName("omc_items:warp_stick").getBest(),
                            (city, ignore) -> Component.text("Poser un /city warp")
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 2,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, ignore) -> Component.text("Avoir sa Mascotte Niveau 2")
                    ),
                    new CommandRequirement("/city create", 1),
                    new ItemDepositRequirement(ItemStack.of(Material.GOLD_INGOT), 128)
            ),
            60 * 5
    ),
    LEVEL_3(
            Component.text("Niveau 3"),
            Component.text("Les Fondations"),
            List.of(

            ),
            60 * 5
    ),
    LEVEL_4(
            Component.text("Niveau 4"),
            Component.text("Les Fondations"),
            List.of(

            ),
            60 * 5
    ),
    LEVEL_5(
            Component.text("Niveau 5"),
            Component.text("Les Fondations"),
            List.of(
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 5,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal() + 1) {
                                    return Component.text("Avoir 5 Chunks");
                                }

                                return Component.text(String.format(
                                        "Avoir 5 Chunks (%d/5)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getLaw().getWarp() != null,
                            city -> CustomItemRegistry.getByName("omc_items:warp_stick").getBest(),
                            (city, ignore) -> Component.text("Poser un /city warp")
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 2,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, ignore) -> Component.text("Avoir sa Mascotte Niveau 2")
                    ),
                    new CommandRequirement("/city create", 1),
                    new ItemDepositRequirement(ItemStack.of(Material.GOLD_INGOT), 128)
            ),
            60 * 5
    ),
    LEVEL_6(
            Component.text("Niveau 6"),
            Component.text("Les Fondations"),
            List.of(
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 5,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal() + 1) {
                                    return Component.text("Avoir 5 Chunks");
                                }

                                return Component.text(String.format(
                                        "Avoir 5 Chunks (%d/5)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getLaw().getWarp() != null,
                            city -> CustomItemRegistry.getByName("omc_items:warp_stick").getBest(),
                            (city, ignore) -> Component.text("Poser un /city warp")
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 2,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, ignore) -> Component.text("Avoir sa Mascotte Niveau 2")
                    ),
                    new CommandRequirement("/city create", 1),
                    new ItemDepositRequirement(ItemStack.of(Material.GOLD_INGOT), 128)
            ),
            60 * 5
    ),
    LEVEL_7(
            Component.text("Niveau 7"),
            Component.text("Les Fondations"),
            List.of(
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 5,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal() + 1) {
                                    return Component.text("Avoir 5 Chunks");
                                }

                                return Component.text(String.format(
                                        "Avoir 5 Chunks (%d/5)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getLaw().getWarp() != null,
                            city -> CustomItemRegistry.getByName("omc_items:warp_stick").getBest(),
                            (city, ignore) -> Component.text("Poser un /city warp")
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 2,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, ignore) -> Component.text("Avoir sa Mascotte Niveau 2")
                    ),
                    new CommandRequirement("/city create", 1),
                    new ItemDepositRequirement(ItemStack.of(Material.GOLD_INGOT), 128)
            ),
            60 * 5
    ),
    LEVEL_8(
            Component.text("Niveau 8"),
            Component.text("Les Fondations"),
            List.of(
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 5,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal() + 1) {
                                    return Component.text("Avoir 5 Chunks");
                                }

                                return Component.text(String.format(
                                        "Avoir 5 Chunks (%d/5)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getLaw().getWarp() != null,
                            city -> CustomItemRegistry.getByName("omc_items:warp_stick").getBest(),
                            (city, ignore) -> Component.text("Poser un /city warp")
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 2,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, ignore) -> Component.text("Avoir sa Mascotte Niveau 2")
                    ),
                    new CommandRequirement("/city create", 1),
                    new ItemDepositRequirement(ItemStack.of(Material.GOLD_INGOT), 128)
            ),
            60 * 5
    ),
    LEVEL_9(
            Component.text("Niveau 9"),
            Component.text("Les Fondations"),
            List.of(
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 5,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal() + 1) {
                                    return Component.text("Avoir 5 Chunks");
                                }

                                return Component.text(String.format(
                                        "Avoir 5 Chunks (%d/5)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getLaw().getWarp() != null,
                            city -> CustomItemRegistry.getByName("omc_items:warp_stick").getBest(),
                            (city, ignore) -> Component.text("Poser un /city warp")
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 2,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, ignore) -> Component.text("Avoir sa Mascotte Niveau 2")
                    ),
                    new CommandRequirement("/city create", 1),
                    new ItemDepositRequirement(ItemStack.of(Material.GOLD_INGOT), 128)
            ),
            60 * 5
    ),
    LEVEL_10(
            Component.text("Niveau 10"),
            Component.text("Les Fondations"),
            List.of(
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 5,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal() + 1) {
                                    return Component.text("Avoir 5 Chunks");
                                }

                                return Component.text(String.format(
                                        "Avoir 5 Chunks (%d/5)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getLaw().getWarp() != null,
                            city -> CustomItemRegistry.getByName("omc_items:warp_stick").getBest(),
                            (city, ignore) -> Component.text("Poser un /city warp")
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 2,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, ignore) -> Component.text("Avoir sa Mascotte Niveau 2")
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
    private final long upgradeTime;

    CityLevels(Component name, Component description, List<CityRequirement> requirements, long upgradeTime) {
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        this.upgradeTime = upgradeTime;
    }
}
