package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.api.packetmenulib.events.InventoryClickEvent;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityRequirement;
import fr.openmc.core.features.contest.managers.ContestManager;
import fr.openmc.core.features.contest.managers.ContestPlayerManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

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
    public ItemStack getIcon() {
        return itemType;
    }

    @Override
    public Component getName() {
        return Component.text("Déposer " + amountRequired + " ").append(ItemUtils.getItemTranslation(itemType));
    }

    @Override
    public Component getDescription() {
        return Component.text("§e§lCLIQUEZ ICI POUR DEPOSER");
    }

    public void runAction(City city, InventoryClickEvent e) {
        ;
        int shellCount = Arrays.stream(player.getInventory().getContents()).filter(is -> is != null && is.isSimilar(shellContestItem)).mapToInt(ItemStack::getAmount).sum();

        if (ItemUtils.hasEnoughItems(player, shellContestItem, shellCount)) {
            ItemUtils.removeItemsFromInventory(player, shellContestItem, shellCount);

            int newPlayerPoints = shellCount + ContestManager.dataPlayer.get(player.getUniqueId()).getPoints();
            int updatedCampPoints = shellCount + ContestManager.data.getInteger("points" + ContestManager.dataPlayer.get(player.getUniqueId()).getCamp());

            ContestPlayerManager.setPointsPlayer(player.getUniqueId(), newPlayerPoints);
            String pointCamp = "points" + ContestManager.dataPlayer.get(player.getUniqueId()).getCamp();
            if (Objects.equals(pointCamp, "points1")) {
                ContestManager.data.setPoints1(updatedCampPoints);
            } else if (Objects.equals(pointCamp, "points2")) {
                ContestManager.data.setPoints2(updatedCampPoints);
            }

            MessagesManager.sendMessage(getOwner(), Component.text("§7Vous avez déposé§b " + shellCount + " Coquillage(s) de Contest§7 pour votre Team!"), Prefix.CONTEST, MessageType.SUCCESS, true);
        } else {
            MessagesManager.sendMessage(getOwner(), Component.text("§cVous n'avez pas de Coquillage(s) de Contest§7"), Prefix.CONTEST, MessageType.ERROR, true);
        }

    }
}
