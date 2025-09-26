package fr.openmc.core.features.dream.mobs;

import fr.openmc.core.features.dream.items.DreamItem;
import lombok.Getter;

@Getter
public class DreamLoot {
    private final DreamItem item;
    private final double chance;
    private final int minAmount;
    private final int maxAmount;

    public DreamLoot(DreamItem item, double chance, int minAmount, int maxAmount) {
        this.item = item;
        this.chance = chance;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }
}
