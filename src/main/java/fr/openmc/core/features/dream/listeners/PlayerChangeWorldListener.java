package fr.openmc.core.features.dream.listeners;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import fr.openmc.core.features.displays.scoreboards.ScoreboardManager;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.displays.DreamBossBar;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.models.DreamStats;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangeWorldListener implements Listener {

    @EventHandler
    public void onDreamEntrered(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getLocation().getWorld();

        if (!world.getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        ScoreboardManager.removePlayerScoreboard(player);
        ScoreboardManager.createNewScoreboard(player);

        for (BossbarsType type : BossbarsType.values()) {
            BossbarManager.removeBossBar(type, player);
        }

        DreamManager.addPlayer(player);
        DreamStats dreamStats = DreamManager.getDreamStats(player);

        if (dreamStats == null) return;

        DreamBossBar.addDreamBossBarForPlayer(player, (float) dreamStats.getDreamTime() / dreamStats.getMaxDreamTime());
    }

    @EventHandler
    public void onDreamLeave(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!event.getFrom().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        ScoreboardManager.removePlayerScoreboard(player);
        ScoreboardManager.createNewScoreboard(player);

        for (BossbarsType type : BossbarsType.values()) {
            if (type.equals(BossbarsType.DREAM)) continue;

            BossbarManager.addBossBar(type, BossbarManager.bossBarHelp, player);
        }

        BossbarManager.removeBossBar(BossbarsType.DREAM, player);
    }
}
