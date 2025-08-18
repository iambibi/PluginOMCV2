package fr.openmc.core.features.city.sub.milestone.rewards;

import fr.openmc.core.features.city.sub.milestone.CityRewards;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class MascotsSkinUnlockRewards implements CityRewards {

    private final String command;
    private final int amountRequired;

    public MascotsSkinUnlockRewards(String command, int amountRequired) {
        this.command = command;
        this.amountRequired = amountRequired;
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public Component getName() {
        return null;
    }

    @Override
    public Component getDescription() {
        return null;
    }

    @Override
    public Runnable getMethods() {
        return null;
    }
}
