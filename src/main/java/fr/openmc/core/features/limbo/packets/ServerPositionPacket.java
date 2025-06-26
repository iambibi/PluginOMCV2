package fr.openmc.core.features.limbo.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ServerPositionPacket {
    private static ProtocolManager protocolManager;

    public ServerPositionPacket(ProtocolManager manager) {
        protocolManager = manager;
    }

    public static void send(Player player, Location location) {
        PacketContainer positionPacket = new PacketContainer(PacketType.Play.Server.POSITION);
        Vec3 position = new Vec3(location.getX(), location.getY(), location.getZ());
        PositionMoveRotation positionMoveRotation = new PositionMoveRotation(position, Vec3.ZERO,
                location.getPitch(), location.getYaw());

        positionPacket.getStructures()
                .withType(PositionMoveRotation.class)
                .write(0, positionMoveRotation);

        try {
            protocolManager.sendServerPacket(player, positionPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
