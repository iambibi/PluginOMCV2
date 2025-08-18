package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityRequirement;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;
import java.util.function.Predicate;

public class TemplateRequirement implements CityRequirement {
    private final Predicate<City> eqBool;
    private final Function<City, ItemStack> item;
    private final Function<City, Component> name;


    public TemplateRequirement(Predicate<City> isDone, Function<City, ItemStack> item, Function<City, Component> name) {
        this.eqBool = isDone;
        this.item = item;
        this.name = name;
    }

    @Override
    public boolean isDone(City city) {
        return eqBool.test(city);
    }

    @Override
    public String getScope() {
        return null;
    }

    @Override
    public ItemStack getIcon(City city) {
        return item.apply(city);
    }

    @Override
    public Component getName(City city) {
        return name.apply(city);
    }

    @Override
    public Component getDescription() {
        return null;
    }
}
