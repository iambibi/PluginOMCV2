package fr.openmc.core.features.limbo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.openmc.core.OMCPlugin;
import org.bukkit.entity.Player;

public class LimboPacketBlocker {

    public static void register(ProtocolManager protocolManager) {
        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Client.CHAT_COMMAND) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (!LimboManager.isInLimbo(player.getUniqueId())) return;

                String command = event.getPacket().getStrings().read(0);
                if (!command.equalsIgnoreCase("/limbo")) {
                    event.setCancelled(true);
                }
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (!LimboManager.isInLimbo(player.getUniqueId())) return;
                event.setCancelled(true);
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Client.TAB_COMPLETE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (!LimboManager.isInLimbo(player.getUniqueId())) return;
                event.setCancelled(true);
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (!LimboManager.isInLimbo(player.getUniqueId())) return;
                event.setCancelled(true);
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.HIGHEST,
                PacketType.Play.Client.POSITION,
                PacketType.Play.Client.POSITION_LOOK,
                PacketType.Play.Client.LOOK) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (LimboManager.isInLimbo(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.HIGHEST,
                PacketType.Play.Server.SYSTEM_CHAT,
                PacketType.Play.Server.CHAT) {

            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();

                if (LimboManager.isInLimbo(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Server.TAB_COMPLETE) {

            @Override
            public void onPacketSending(PacketEvent event) {
                if (LimboManager.isInLimbo(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.HIGHEST,
                PacketType.Play.Server.WINDOW_ITEMS,
                PacketType.Play.Server.SET_SLOT,
                PacketType.Play.Server.OPEN_WINDOW) {

            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();

                if (LimboManager.isInLimbo(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.HIGHEST,
                PacketType.Play.Client.WINDOW_CLICK) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (LimboManager.isInLimbo(player.getUniqueId())) {
                    event.setCancelled(true); // Bloque toute interaction
                }
            }
        });
    }


}
