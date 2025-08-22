package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum CityBankLimitRewards implements CityRewards {

    LEVEL_1(null),
    LEVEL_2(5000),
    LEVEL_3(10000),
    LEVEL_4(15000),
    LEVEL_5(30000),
    LEVEL_6(50000),
    LEVEL_7(100000),
    LEVEL_8(null),
    LEVEL_9(175000),
    LEVEL_10(250000);


    private final Integer bankBalanceLimit;

    CityBankLimitRewards(Integer bankBalanceLimit) {
        this.bankBalanceLimit = bankBalanceLimit;
    }

    public static int getBankBalanceLimit(int level) {
        CityBankLimitRewards[] values = CityBankLimitRewards.values();

        if (level < 1 || level > values.length) {
            throw new IllegalArgumentException("Niveau invalide: " + level);
        }

        CityBankLimitRewards reward = values[level - 1];
        if (reward.bankBalanceLimit != null) {
            return reward.bankBalanceLimit;
        }

        for (int i = level - 2; i >= 0; i--) {
            if (values[i].bankBalanceLimit != null) {
                return values[i].bankBalanceLimit;
            }
        }

        return 0;
    }

    @Override
    public Component getName() {
        return Component.text("§7Limite à §6" + bankBalanceLimit + " d'Argent §7dans la §ebanque");
    }
}
