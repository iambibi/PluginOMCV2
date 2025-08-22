package fr.openmc.core.features.city.sub.milestone.commands;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class AdminCityMilestoneCommands {
    @Command({"admcity milestone skipUpgrade"})
    @CommandPermission("omc.admins.commands.milestone")
    @Description("Skip l'upgrade d'un level")
    void adminSkinUpgrade(Player sender, String uuid) {
        City city = CityManager.getCity(uuid);

        if (city == null) {
            MessagesManager.sendMessage(sender, Component.text("§cVille inexistante"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessage(sender, Component.text("Upgrade du level skip"), Prefix.STAFF, MessageType.SUCCESS, false);
        DynamicCooldownManager.clear(uuid, "city:upgrade-level", true);
    }

}
