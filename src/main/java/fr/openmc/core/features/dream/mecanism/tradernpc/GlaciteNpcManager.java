package fr.openmc.core.features.dream.mecanism.tradernpc;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.api.hooks.FancyNpcsHook;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class GlaciteNpcManager implements Listener {
    public GlaciteNpcManager() {
        // fetch les npcs apres 30 secondes le temps que fancy npc s'initialise.
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            FancyNpcsPlugin.get().getNpcManager().getAllNpcs().forEach(npc -> {
                if (!npc.getData().getName().startsWith("glacite-")) {
                    if (DreamDimensionManager.hasSeedChanged()) {
                        FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
                        npc.removeForAll();
                    }
                }
            });
        }, 20L * 30);
    }

    public static void createNPC(Location locationNpc) {
        if (!FancyNpcsHook.isHasFancyNpc()) return;
        UUID npcUUID = UUID.randomUUID();
        UUID creatorUUID = UUID.randomUUID();

        NpcData data = new NpcData("glacite-" + npcUUID, creatorUUID, locationNpc);
        data.setDisplayName("§bVagabond Glacial");
        data.setSkin("https://s.namemc.com/i/18e45c2529931568.png");
        data.setTurnToPlayer(true);
        data.setTurnToPlayerDistance(10);
        data.setShowInTab(false);

        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);
        npc.create();
        npc.spawnForAll();
    }

    @EventHandler
    public void onInteract(NpcInteractEvent event) {
        if (!FancyNpcsHook.isHasFancyNpc()) return;

        Player player = event.getPlayer();

        Npc npc = event.getNpc();

        if (!npc.getData().getName().startsWith("glacite-")) return;

        new GlaciteTradeMenu(player).open();
    }
}