package fr.openmc.core.features.city.sub.mascots.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.settings.PlayerSettingsManager;
import fr.openmc.core.features.settings.SettingType;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

public class MascotsSoundListener {

    public MascotsSoundListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(OMCPlugin.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_SOUND) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        Player player = event.getPlayer();
                        if (PlayerSettingsManager.getPlayerSettings(player.getUniqueId()).getSetting(SettingType.MASCOT_PLAY_SOUND_POLICY)) {

                            int entityId = event.getPacket().getIntegers().read(0);

                            ServerLevel nmsLevel = ((CraftWorld) player.getWorld()).getHandle();

                            net.minecraft.world.entity.Entity nmsEntity = nmsLevel.getEntity(entityId);

                            if (nmsEntity != null && MascotsManager.mascotsByEntityUUID.containsKey(nmsEntity.getUUID())) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
        );
    }
}
