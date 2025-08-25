package fr.openmc.core.features.dream;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class DreamBossBar {

    private static final Component TEXTURE_BOSSBAR = Component.text("");

    /**
     * Adds a tutorial boss bar for the player with the given message and progress.
     *
     * @param player   The player to add the boss bar for.
     * @param progress The progress of the dream (0.0 to 1.0).
     */
    public static void addDreamBossBarForPlayer(Player player, float progress) {
        BossBar bar = BossBar.bossBar(
                TEXTURE_BOSSBAR,
                progress,
                BossBar.Color.BLUE,
                BossBar.Overlay.PROGRESS
        );
        BossbarManager.addBossBar(BossbarsType.DREAM, bar, player);
    }

    /**
     * Updates the tutorial boss bar for the player with the given message and progress.
     *
     * @param player   The player to update the boss bar for.
     * @param progress The new progress of the tutorial step (0.0 to 1.0).
     */
    public static void update(Player player, float progress) {
        BossBar bar = BossbarManager.getBossBar(BossbarsType.DREAM, player);

        if (bar != null) {
            bar.progress(progress);
        }
    }

    /**
     * Hides the tutorial boss bar for the player.
     *
     * @param player The player to hide the boss bar for.
     */
    public static void hide(Player player) {
        BossbarManager.removeBossBar(BossbarsType.DREAM, player);
    }
}
