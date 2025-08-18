package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityRequirement;
import fr.openmc.core.features.city.sub.statistics.CityStatisticsManager;
import fr.openmc.core.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ItemDepositRequirement implements CityRequirement {
    private final ItemStack itemType;
    private final int amountRequired;

    public ItemDepositRequirement(ItemStack itemType, int amountRequired) {
        this.itemType = itemType;
        this.amountRequired = amountRequired;
    }

    @Override
    public boolean isDone(City city) {
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
    public Component getName(City city) {
        return Component.text("Déposer " + amountRequired + " ").append(ItemUtils.getItemTranslation(itemType));
    }

    @Override
    public Component getDescription() {
        return Component.text("§e§lCLIQUEZ ICI POUR DEPOSER");
    }

    public void runAction(City city, InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        int itemRequiredCount = Arrays.stream(player.getInventory().getContents()).filter(is -> is != null && ItemUtils.isSimilar(is, itemType)).mapToInt(ItemStack::getAmount).sum();

        if (ItemUtils.hasEnoughItems(player, itemType, itemRequiredCount)) {
            ItemUtils.removeItemsFromInventory(player, itemType, itemRequiredCount);

            CityStatisticsManager.increment(city.getUUID(), getScope(), itemRequiredCount);
        }
    }
}
