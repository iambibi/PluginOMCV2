package fr.openmc.core.features.dream;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class DreamScoreboard {
    public static void updateDreamScoreboard(Player player, Scoreboard scoreboard, Objective objective) {
        DreamBiome dreamBiome = DreamDimensionManager.getDreamBiome(player);

        if (dreamBiome == null) return;

        objective.getScore("§7").setScore(19);
        objective.getScore("§8• §fNom: §7" + player.getName()).setScore(18);
        objective.getScore("   ").setScore(17);
        objective.getScore("§8• §fLocation: §7" + dreamBiome.getName()).setScore(16);

        // temps
        // nb orbe

        objective.getScore("   ").setScore(1);
        objective.getScore("§1      ᴘʟᴀʏ.ᴏᴘᴇɴᴍᴄ.ꜰʀ").setScore(0);
    }

}
