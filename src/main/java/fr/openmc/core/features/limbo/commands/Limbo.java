package fr.openmc.core.features.limbo.commands;

import fr.openmc.core.features.limbo.LimboManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"limbo"})
@Description("Les abysses")
@CommandPermission("omc.commands.limbo")
public class Limbo {
    private final LimboManager limboManager;

    public Limbo(LimboManager limboManager) {
        this.limboManager = limboManager;
    }
    @DefaultFor("~")
    public void limboMain(Player player) {
        limboManager.returnFromLimbo(player);
    }

    @Subcommand("send")
    @Description("Envoie un joueur dans le Limbo")
    @CommandPermission("omc.admin.commands.limbo.send")
    public void sendPlayerLimbo(Player sender, Player player) {
        limboManager.sendToLimbo(player);
        player.sendMessage("§7Tu as été envoyé en limbo...");

    }
}
