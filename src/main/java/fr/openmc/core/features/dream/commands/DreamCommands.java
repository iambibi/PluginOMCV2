package fr.openmc.core.features.dream.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.events.DreamTimeEndEvent;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("dream")
@CommandPermission("omc.commands.dream")
public class DreamCommands {
    @Subcommand("leave")
    @CommandPermission("omc.commands.dream.leave")
    public void get(Player player) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getServer().getPluginManager().callEvent(new DreamTimeEndEvent(player))
        );

        MessagesManager.sendMessage(player, Component.text("Vous avez quitté votre rêve avec succès."), Prefix.DREAM, MessageType.SUCCESS, false);
    }
}
