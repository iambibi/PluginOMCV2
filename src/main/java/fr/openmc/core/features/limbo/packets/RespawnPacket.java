package fr.openmc.core.features.limbo.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameType;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Optional;

import static fr.openmc.core.features.limbo.LimboManager.limboWorld;

public class RespawnPacket {
    private static ProtocolManager protocolManager;

    public RespawnPacket(ProtocolManager manager) {
        protocolManager = manager;
    }

    private static CommonPlayerSpawnInfo getCommonPlayerSpawnInfo() {
        ServerLevel limboServerLevel = ((CraftWorld) limboWorld).getHandle();

        return new CommonPlayerSpawnInfo(
                limboServerLevel.dimensionTypeRegistration(),
                limboServerLevel.dimension(),
                limboServerLevel.getSeed(),
                GameType.ADVENTURE, // Mode aventure pour empêcher interactions
                null, // previous gamemode
                false, // debug
                limboServerLevel.isFlat(), // flat
                Optional.empty(), // last death location
                0, // portal cooldown
                limboServerLevel.getSeaLevel()
        );
    }

    public static void send(Player player) {
        PacketContainer respawnPacket = new PacketContainer(PacketType.Play.Server.RESPAWN);
        respawnPacket.getStructures().withType(CommonPlayerSpawnInfo.class)
                .write(0, getCommonPlayerSpawnInfo());

        try {
            protocolManager.sendServerPacket(player, respawnPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
