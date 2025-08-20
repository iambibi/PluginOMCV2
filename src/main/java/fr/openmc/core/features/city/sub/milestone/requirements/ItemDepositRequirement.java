package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.api.menulib.Menu;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.milestone.CityRequirement;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ItemDepositRequirement implements CityRequirement {
    private final ItemStack itemType;
    private final int amountRequired;

    public ItemDepositRequirement(ItemStack itemType, int amountRequired) {
        this.itemType = itemType;
        this.amountRequired = amountRequired;
    }

    @Override
    public boolean isPredicateDone(City city) {
        return false;
    }

    @Override
    public String getScope() {
        return "deposit_" + itemType.getType().toString().toLowerCase();
    }

    @Override
    public ItemStack getIcon(City city) {
        return itemType;
    }

    @Override
    public Component getName(City city, CityLevels level) {
        if (city.getLevel() > level.ordinal() + 1) {
            return Component.text(String.format(
                    "Déposer %d %s",
                    amountRequired,
                    ItemUtils.getItemName(itemType)
            ));
        }

        return Component.text(String.format(
                "Déposer %d %s (%d/%d)",
                amountRequired,
                ItemUtils.getItemName(itemType),
                Objects.requireNonNull(
                        CityStatisticsManager.getOrCreateStat(city.getUUID(), getScope())
                ).asInt(),
                amountRequired
        ));
    }

    @Override
    public Component getDescription() {
        return Component.text("§e§lCLIQUEZ ICI POUR DEPOSER");
    }

    public void runAction(Menu menu, City city, InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        int current = Objects.requireNonNull(
                CityStatisticsManager.getOrCreateStat(city.getUUID(), getScope())
        ).asInt();

        int remaining = amountRequired - current;
        if (remaining <= 0) return;

        int removed = ItemUtils.removeItemsFromInventory(player, itemType, remaining);

        if (removed > 0) {
            CityStatisticsManager.increment(city.getUUID(), getScope(), removed);
            menu.open();
        }
    }
}
