package fr.openmc.core.features.limbo.packets;

import com.comphenix.protocol.ProtocolManager;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ChunkPacket {
    private static ProtocolManager protocolManager;

    public ChunkPacket(ProtocolManager manager) {
        protocolManager = manager;
    }

    public static void send(Player player, LevelChunk levelChunk) {
        try {
            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            connection.send(new ClientboundLevelChunkWithLightPacket(levelChunk, levelChunk.getLevel().getLightEngine(), null, null, false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
