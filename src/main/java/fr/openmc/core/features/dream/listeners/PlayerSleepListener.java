package fr.openmc.core.features.dream.listeners;

import fr.openmc.core.features.dream.DreamManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.world.TimeSkipEvent;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class PlayerSleepListener implements Listener {

    private Set<UUID> playersDreaming = new HashSet<>();

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();

        if (!event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) return;

        Random random = new Random();

        if (random.nextDouble() > DreamManager.calculateDreamProbability(player)) return;


    }

    @EventHandler
    public void onNightSkip(TimeSkipEvent event) {
        if (event.getSkipReason() == TimeSkipEvent.SkipReason.NIGHT_SKIP) playersDreaming.clear();
    }
}
