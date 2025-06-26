package fr.openmc.core.features.limbo.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public class HealthPacket {
    private static ProtocolManager protocolManager;

    public HealthPacket(ProtocolManager manager) {
        protocolManager = manager;
    }

    public static void send(Player player, float health, Integer food) {
        PacketContainer healthPacket = new PacketContainer(PacketType.Play.Server.UPDATE_HEALTH);
        healthPacket.getFloat().write(0, health); // health
        healthPacket.getIntegers().write(0, food); // food

        try {
            protocolManager.sendServerPacket(player, healthPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
