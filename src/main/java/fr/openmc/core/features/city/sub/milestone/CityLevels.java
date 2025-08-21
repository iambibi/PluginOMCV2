package fr.openmc.core.features.city.sub.milestone;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.managers.NPCManager;
import fr.openmc.core.features.city.sub.milestone.requirements.CommandRequirement;
import fr.openmc.core.features.city.sub.milestone.requirements.EventTemplateRequirement;
import fr.openmc.core.features.city.sub.milestone.requirements.ItemDepositRequirement;
import fr.openmc.core.features.city.sub.milestone.requirements.TemplateRequirement;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.items.CustomItemRegistry;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

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
                    new CommandRequirement("/city map", 1),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 5,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 5 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 5 Claims (%d/5)",
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
                    new ItemDepositRequirement(Material.GOLD_INGOT, 128)
            ),
            60 * 10
    ),
    LEVEL_3(
            Component.text("Niveau 3"),
            Component.text("Ville peu développé"),
            List.of(
                    new CommandRequirement("/city bank", 1),
                    new CommandRequirement("/city chest", 1),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 10,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 10 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 10 Claims (%d/10)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 5000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 5k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 5k dans la banque (%s/5k)",
                                        EconomyManager.getFormattedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMembers().size() >= 2,
                            city -> ItemStack.of(Material.PLAYER_HEAD),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 2 Membres");
                                }

                                return Component.text(String.format(
                                        "Avoir 2 Membres (%d/2)",
                                        city.getMembers().size()
                                ));
                            }
                    ),
                    new ItemDepositRequirement(Material.DIAMOND, 16)
            ),
            60 * 30
    ),
    LEVEL_4(
            Component.text("Niveau 4"),
            Component.text("Démocratie"),
            List.of(
                    new TemplateRequirement(
                            city -> !NotationManager.cityNotations.get(city.getUUID()).isEmpty(),
                            city -> ItemStack.of(Material.DIAMOND),
                            (city, level) -> Component.text("Recevoir une Notation")
                    ),
                    new TemplateRequirement(
                            city -> city.getRanks().size() >= 2,
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 2 Grades (/city rank)");
                                }

                                return Component.text(String.format(
                                        "Avoir 2 Grades (%d/2)",
                                        city.getRanks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 7500,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 7,5k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 7,5k dans la banque (%s/7,5k)",
                                        EconomyManager.getFormattedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_items:aywenite").getBest(), 128),
                    new ItemDepositRequirement(Material.GRAY_WOOL, 32),
                    new ItemDepositRequirement(Material.GLASS, 128),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_items:suit_helmet").getBest(), 1),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_items:suit_chestplate").getBest(), 1),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_items:suit_leggings").getBest(), 1),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_items:suit_boots").getBest(), 1),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_foods:courgette").getBest(), 8),
                    new EventTemplateRequirement(
                            (city, scope) -> Objects.requireNonNull(CityStatisticsManager
                                            .getOrCreateStat(city.getUUID(), scope))
                                    .asInt() >= 1,

                            city -> CustomItemRegistry.getByName("omc_blocks:urne").getBest(),

                            (city, level, scope) -> Component.text("Craftez une Urne"),
                            "craft_urne",
                            CraftItemEvent.class,
                            (event, scope) -> {
                                CraftItemEvent eventCraft = (CraftItemEvent) event;
                                ItemStack item = eventCraft.getCurrentItem();
                                if (item == null || !item.isSimilar(CustomItemRegistry.getByName("omc_blocks:urne").getBest()))
                                    return;

                                Player player = (Player) eventCraft.getWhoClicked();
                                City playerCity = CityManager.getPlayerCity(player.getUniqueId());

                                if (Objects.requireNonNull(CityStatisticsManager.getOrCreateStat(playerCity.getUUID(), scope)).asInt() >= 1)
                                    return;

                                CityStatisticsManager.increment(playerCity.getUUID(), scope, 1);
                            }
                    )
            ),
            60 * 90
    ),
    LEVEL_5(
            Component.text("Niveau 5"),
            Component.text("Développement Economique"),
            List.of(
                    new TemplateRequirement(
                            city -> NPCManager.hasNPCS(city.getUUID()),
                            city -> CustomItemRegistry.getByName("omc_blocks:urne").getBest(),
                            (city, level) -> Component.text("Poser l'Urne")
                    ),

                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUUID()).stream().anyMatch(notation -> notation.getTotalNote() >= 10),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 10 points sur une des Notations")
                    ),
                    new CommandRequirement("/city mayor", 1),
                    new ItemDepositRequirement(Material.GOLD_BLOCK, 32),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 12000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 12k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 12k dans la banque (%s/12k)",
                                        EconomyManager.getFormattedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 20,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 20 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 20 Claims (%d/20)",
                                        city.getChunks().size()
                                ));
                            }
                    )
            ),
            60 * 60 * 3
    ),
    LEVEL_6(
            Component.text("Niveau 6"),
            Component.text("Capitale"),
            List.of(
                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUUID()).stream().anyMatch(notation -> notation.getTotalNote() >= 20),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 20 points sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 20000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 20k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 20k dans la banque (%s/20k)",
                                        EconomyManager.getFormattedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 25,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 25 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 25 Claims (%d/25)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 5,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> Component.text("Etre level 5 sur la Mascotte")
                    ),
                    new ItemDepositRequirement(Material.STONE_BRICKS, 400),
                    new ItemDepositRequirement(Material.BLACK_CONCRETE, 184),
                    new ItemDepositRequirement(Material.WHITE_CONCRETE, 64),
                    new ItemDepositRequirement(Material.DIAMOND, 64)
            ),
            60 * 60 * 5
    ),
    LEVEL_7(
            Component.text("Niveau 7"),
            Component.text("Royaume ?"),
            List.of(
                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUUID()).stream().anyMatch(notation -> notation.getTotalNote() >= 30),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 30 points sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 30000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 30k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 30k dans la banque (%s/30k)",
                                        EconomyManager.getFormattedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 30,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 30 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 30 Claims (%d/30)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 6,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> Component.text("Etre level 6 sur la Mascotte")
                    ),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_items:aywenite").getBest(), 400),
                    new ItemDepositRequirement(Material.DIAMOND_SWORD, 10),
                    new ItemDepositRequirement(Material.TNT, 10)
            ),
            60 * 60 * 10
    ),
    LEVEL_8(
            Component.text("Niveau 8"),
            Component.text("Empire ?"),
            List.of(
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUUID()).getNumberWar() >= 2,
                            city -> ItemStack.of(Material.IRON_SWORD),
                            (city, level) -> Component.text("Avoir fait 2 guerre")
                    ),
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUUID()).getNumberWon() >= 1,
                            city -> ItemStack.of(Material.DIAMOND_SWORD),
                            (city, level) -> Component.text("Gagner une guerre")
                    ),
                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUUID()).stream().anyMatch(notation -> notation.getTotalNote() >= 40),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 40 points sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 60000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 60k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 60k dans la banque (%s/60k)",
                                        EconomyManager.getFormattedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getChunks().size() >= 50,
                            city -> ItemStack.of(Material.OAK_FENCE),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 50 Claims");
                                }

                                return Component.text(String.format(
                                        "Avoir 50 Claims (%d/50)",
                                        city.getChunks().size()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 7,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> Component.text("Etre level 7 sur la Mascotte")
                    ),
                    new ItemDepositRequirement(Material.NETHERITE_INGOT, 4),
                    new ItemDepositRequirement(Material.OBSIDIAN, 128)
            ),
            60 * 60 * 16
    ),
    LEVEL_9(
            Component.text("Niveau 9"),
            Component.text("Puissance militaire"),
            List.of(
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUUID()).getNumberWon() >= 3,
                            city -> ItemStack.of(Material.DIAMOND_SWORD),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Gagner 3 guerres");
                                }

                                return Component.text(String.format(
                                        "Gagner 3 guerres (%s/3)",
                                        WarManager.warHistory.get(city.getUUID()).getNumberWon()
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUUID()).stream().anyMatch(notation -> notation.getTotalNote() >= 50),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 50 points sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 80000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 80k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 80k dans la banque (%s/80k)",
                                        EconomyManager.getFormattedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 8,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> Component.text("Etre level 8 sur la Mascotte")
                    ),
                    new ItemDepositRequirement(Material.DIAMOND, 300),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_foods:kebab").getBest(), 128)
            ),
            60 * 60 * 24
    ),
    LEVEL_10(
            Component.text("Niveau 10"),
            Component.text("Métropole"),
            List.of(
                    new TemplateRequirement(
                            city -> NotationManager.top10Cities.contains(city.getUUID()),
                            city -> ItemStack.of(Material.HONEYCOMB),
                            (city, level) -> Component.text("Etre dans le top 10 des notations sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> NotationManager.cityNotations.get(city.getUUID()).stream().anyMatch(notation -> notation.getTotalNote() >= 60),
                            city -> ItemStack.of(Material.DANDELION),
                            (city, level) -> Component.text("Avoir minimum 60 points sur une des Notations")
                    ),
                    new TemplateRequirement(
                            city -> WarManager.warHistory.get(city.getUUID()).getNumberWar() >= 10,
                            city -> ItemStack.of(Material.NETHERITE_SWORD),
                            (city, level) -> Component.text("Avoir fait 10 guerres")
                    ),
                    new TemplateRequirement(
                            city -> city.getBalance() >= 125000,
                            city -> ItemStack.of(Material.GOLD_BLOCK),
                            (city, level) -> {
                                if (city.getLevel() > level.ordinal()) {
                                    return Component.text("Avoir 125k dans la banque");
                                }

                                return Component.text(String.format(
                                        "Avoir 125k dans la banque (%s/125k)",
                                        EconomyManager.getFormattedNumber(city.getBalance())
                                ));
                            }
                    ),
                    new TemplateRequirement(
                            city -> city.getMascot().getLevel() >= 9,
                            city -> ItemStack.of(city.getMascot().getMascotEgg()),
                            (city, level) -> Component.text("Etre level 9 sur la Mascotte")
                    ),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_blocks:aywenite_block").getBest(), 32),
                    new ItemDepositRequirement(CustomItemRegistry.getByName("omc_contest:contest_shell").getBest(), 128),
                    new ItemDepositRequirement(Material.SCULK, 1028)
            ),
            60 * 60 * 24 * 2
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

    public boolean isCompleted(City city) {
        for (CityRequirement requirement : requirements) {
            if (!requirement.isDone(city, this)) return false;
        }
        return true;
    }

    public void runUpgradeTime(City city) {
        DynamicCooldownManager.use(city.getUUID(), "city:upgrade-level", upgradeTime * 1000);
    }
}
