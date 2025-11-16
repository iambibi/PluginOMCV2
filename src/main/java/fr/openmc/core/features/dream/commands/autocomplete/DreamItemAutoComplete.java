package fr.openmc.core.features.dream.commands.autocomplete;

import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public class DreamItemAutoComplete implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        List<String> result = new ArrayList<>();

        for (String name : DreamItemRegistry.getNames()) {
            String prefix = "omc_dream:";
            int prefixLength = prefix.length();

            if (name.length() > prefixLength && name.startsWith(prefix)) {
                result.add(name.substring(prefixLength));
            } else {
                result.add(name);
            }
        }

        return result;
    }
}
