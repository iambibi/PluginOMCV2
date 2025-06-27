package fr.openmc.core.features.limbo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;
import dev.lone.itemsadder.api.ItemsAdder;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.limbo.commands.Limbo;
import fr.openmc.core.features.limbo.dimension.LimboDimensionManager;
import fr.openmc.core.features.limbo.packets.*;
import fr.openmc.core.features.scoreboards.ScoreboardManager;
import fr.openmc.core.utils.SchematicsUtils;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class LimboManager {
    private static ProtocolManager protocolManager = null;

    static Map<UUID, PlayerLimboData> limboPlayers = new HashMap<>();
    public static final Set<UUID> blockedPlayers = new HashSet<>();

    public static Location limboSpawn;
    public static World limboWorld = null;

    public LimboManager() {
        protocolManager = ProtocolLibrary.getProtocolManager();

        // World Creation - Only void
        LimboDimensionManager.createLimboDimension();

        // Add Limbo Structure - from Schematics
        SchematicsUtils.extractSchematic("limbo");
        File schemFile = new File(OMCPlugin.getInstance().getDataFolder() + "/schem", "limbo.schem");
        setStructure(schemFile, limboSpawn);

        // REGISTER COMMANDS & LISTENERS
        CommandsManager.getHandler().register(
                new Limbo(this)
        );

        OMCPlugin.registerEvents(
                new LimboListener()
        );

        // REGISTER PACKETS
        new RespawnPacket(protocolManager);
        new ServerPositionPacket(protocolManager);
        new HealthPacket(protocolManager);
        new ServerPingPacket(protocolManager);
        new TablistPacket(protocolManager);

        LimboPacketBlocker.register(protocolManager);
    }


    public static boolean isInLimbo(UUID playerUUID) {
        return limboPlayers.containsKey(playerUUID);
    }

    public void sendToLimbo(Player player) {
        PlayerLimboData data = new PlayerLimboData(player);
        limboPlayers.put(player.getUniqueId(), data);

        ScoreboardManager.removePlayerScoreboard(player);
        ScoreboardManager.createNewScoreboard(player);

        RespawnPacket.send(player);

        sendLimboChunks(player, limboSpawn, () -> {
            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                HealthPacket.send(player, 1.0f, 20);

                ServerPositionPacket.send(player, limboSpawn);
                blockedPlayers.add(player.getUniqueId());
                ItemsAdder.applyResourcepack(player);
            }, 2L);
        });
    }

    public static void returnFromLimbo(Player player) {
        PlayerLimboData data = limboPlayers.remove(player.getUniqueId());
        if (data == null) return;

        restorePlayer(player, data);
    }

    public static void sendLimboChunks(Player player, Location location, Runnable runnable) {
        int centerX = location.getChunk().getX();
        int centerZ = location.getChunk().getZ();

        for (int x = centerX - 2; x <= centerX + 2; x++) {
            for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                LevelChunk levelChunk = ((CraftWorld) limboWorld).getHandle().getChunk(x, z);
                ChunkPacket.send(player, levelChunk);
            }
        }

        runnable.run();
    }

    private static void restorePlayer(Player player, PlayerLimboData data) {
        // Renvoyer le joueur à son monde d'origine
        CommonPlayerSpawnInfo originalSpawnInfo = new CommonPlayerSpawnInfo(
                data.getDimensionType(),
                data.getDimensionKey(),
                data.getSeed(),
                data.getGameMode(),
                null,
                false,
                false,
                Optional.empty(),
                0,
                data.getSeaLevel()
        );

        PacketContainer returnPacket = new PacketContainer(PacketType.Play.Server.RESPAWN);
        returnPacket.getStructures().withType(CommonPlayerSpawnInfo.class).write(0, originalSpawnInfo);

        try {
            protocolManager.sendServerPacket(player, returnPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Getter
    private static class PlayerLimboData {
        private final Location originalLocation;
        private final Holder<DimensionType> dimensionType;
        private final ResourceKey<Level> dimensionKey;
        private final long seed;
        private final GameType gameMode;
        private final int seaLevel;

        public PlayerLimboData(Player player) {
            this.originalLocation = player.getLocation().clone();
            ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            ServerLevel level = nmsPlayer.serverLevel();

            this.dimensionType = level.dimensionTypeRegistration();
            this.dimensionKey = level.dimension();
            this.seed = level.getSeed();
            this.gameMode = nmsPlayer.gameMode.getGameModeForPlayer();
            this.seaLevel = level.getSeaLevel();
        }
    }

    public void setStructure(File schemFile, Location spawnPoint) {
        Location origin = spawnPoint.clone().subtract(19, 34, 25);
        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
        if (format == null) {
            Bukkit.getLogger().severe("Format inconnu pour le fichier : " + schemFile.getName());
            return;
        }
        try (ClipboardReader reader = format.getReader(new FileInputStream(schemFile))) {
            Clipboard clipboard = reader.read();
            ClipboardHolder holder = new ClipboardHolder(clipboard);

            Transform transform = holder.getTransform();
            Region region = clipboard.getRegion();
            BlockVector3 min = region.getMinimumPoint();
            Extent transformedExtent = new BlockTransformExtent(clipboard, transform);

            for (int x = min.getX(); x <= region.getMaximumPoint().getX(); x++) {
                for (int y = min.getY(); y <= region.getMaximumPoint().getY(); y++) {
                    for (int z = min.getZ(); z <= region.getMaximumPoint().getZ(); z++) {
                        BlockVector3 pos = BlockVector3.at(x, y, z);
                        BlockState block = transformedExtent.getBlock(pos);
                        if (block.getBlockType().getMaterial().isAir()) continue;

                        String blockDataString = block.getAsString();

                        try {
                            BlockData blockData = Bukkit.createBlockData(blockDataString);
                            Vector3 transformedVec = transform.apply(pos.toVector3());
                            Location target = origin.clone().add(
                                    transformedVec.getX() - min.getX(),
                                    transformedVec.getY() - min.getY(),
                                    transformedVec.getZ() - min.getZ()
                            );

                            if (!limboWorld.getBlockAt(target).getChunk().isLoaded()) {
                                limboWorld.getBlockAt(target).getChunk().load();
                            }
                            limboWorld.getBlockAt(target).setBlockData(blockData, false);

                        } catch (IllegalArgumentException e) {
                            Bukkit.getLogger().warning("BlockData invalide : " + blockDataString);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
