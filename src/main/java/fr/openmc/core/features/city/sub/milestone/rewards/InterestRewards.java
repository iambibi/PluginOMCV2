package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum InterestRewards implements CityRewards {

    LEVEL_1(0),
    LEVEL_2(0),
    LEVEL_3(0),
    LEVEL_4(0.2),
    LEVEL_5(0.2),
    LEVEL_6(0.2),
    LEVEL_7(0.1),
    LEVEL_8(0.1),
    LEVEL_9(0.1),
    LEVEL_10(0.4);


    private final double interest;

    InterestRewards(double interest) {
        this.interest = interest;
    }

    public static double getTotalInterest(int level) {
        InterestRewards[] values = InterestRewards.values();

        if (level < 1 || level > values.length) {
            throw new IllegalArgumentException("Niveau invalide: " + level);
        }

        double total = 0;
        for (int i = 0; i < level; i++) {
            total += values[i].interest;
        }
        return total;
    }

    @Override
    public Component getName() {
        return Component.text("§7+ §6" + interest + "% §6d'intérêt");
    }
}
