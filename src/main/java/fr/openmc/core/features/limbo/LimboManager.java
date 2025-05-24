package fr.openmc.core.features.limbo;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
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
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.scoreboards.ScoreboardManager;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;


public class LimboManager {
    //TODO refaire la structure du limbo (classe par paquet)
    public static final List<UUID> playersInLimbo = new ArrayList<>();
    private static final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private static ProtocolManager protocolManager;

    public static final Set<UUID> blockedPlayers = new HashSet<>();

    public LimboManager() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        Bukkit.getPluginManager().registerEvents(new LimboListener(), OMCPlugin.getInstance());

        // filteur de packets
        new LimboPacketBlocker().register();

        extractLimboSchematic();

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL, PacketType.Status.Server.SERVER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                try {
                    ServerStatus originalStatus = packet.getSpecificModifier(ServerStatus.class).read(0);

                    net.minecraft.network.chat.Component motd = originalStatus.description();

                    Optional<ServerStatus.Version> version = originalStatus.version();

                    int realPlayers = (int) Bukkit.getOnlinePlayers().stream()
                            .filter(p -> !playersInLimbo.contains(p.getUniqueId()))
                            .count();

                    ServerStatus.Players players = new ServerStatus.Players(
                            Bukkit.getMaxPlayers(),
                            realPlayers,
                            List.of()
                    );

                    ServerStatus newStatus = new ServerStatus(
                            motd,
                            Optional.of(players),
                            version,
                            originalStatus.favicon(),
                            originalStatus.enforcesSecureChat()
                    );

                    packet.getSpecificModifier(ServerStatus.class).write(0, newStatus);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(OMCPlugin.getInstance(),
                ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                EnumSet<?> actions = packet.getSpecificModifier(EnumSet.class).read(0);
                if (actions.isEmpty()) return;

                Class<? extends Enum> enumClass = actions.iterator().next().getClass();

                boolean shouldFilter = actions.contains(Enum.valueOf(enumClass, "ADD_PLAYER"))
                        || actions.contains(Enum.valueOf(enumClass, "UPDATE_LATENCY"))
                        || actions.contains(Enum.valueOf(enumClass, "UPDATE_LISTED"));
                if (!shouldFilter) return;

                List<Object> entries = packet.getSpecificModifier(List.class).read(0);

                List<Object> filtered = entries.stream()
                        .filter(entry -> {
                            try {
                                UUID profileId = (UUID) entry.getClass().getMethod("profileId").invoke(entry);
                                return !playersInLimbo.contains(profileId);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return true;
                            }
                        })
                        .toList();

                packet.getModifier().withType(List.class).write(0, filtered);
            }
        });
    }


    public void extractLimboSchematic() {
        OMCPlugin plugin = OMCPlugin.getInstance();
        File schemFolder = new File(plugin.getDataFolder(), "schem");
        if (!schemFolder.exists()) {
            schemFolder.mkdirs();
        }

        File outFile = new File(schemFolder, "limbo.schem");
        if (!outFile.exists()) {
            try (InputStream in = plugin.getResource("limbo.schem")) {
                if (in == null) {
                    plugin.getLogger().warning("Le fichier 'libo.schem' est introuvable dans les ressources.");
                    return;
                }

                Files.copy(in, outFile.toPath());
                plugin.getLogger().info("Fichier 'libo.schem' extrait dans plugins/OpenMC/schem/.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isInLimbo(Player player) {
        return playersInLimbo.contains(player.getUniqueId());
    }

    public static void addPlayerToLimbo(Player player) {
        if (!playersInLimbo.contains(player.getUniqueId())) {
            playersInLimbo.add(player.getUniqueId());
            removeFromTabList(player);
        }
    }

    public static void removePlayerToLimbo(Player player) {
        if (!playersInLimbo.contains(player.getUniqueId())) {
            playersInLimbo.remove(player.getUniqueId());
        }
    }


    public static void sendPlayerLimbo(Player player) {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            Bukkit.getLogger().warning("ProtocolLib n'est pas installé, le Limbo ne peut pas être utilisé.");
            return;
        }

        try {
            // on sauvegarde son inventaire pour lui donner quand il sort du limbo, quand il quitte
            saveInventory(player);

            // on envoie le joueur dans une dimension
            sendLoginPacket(player);
            freezePlayer(player);
            removeFromTabList(player);

            System.out.println("Envoi du joueur dans le limbo : " + player.getName());

            // on l'ajoute dans la liste des joueurs etant ds le limbo
            addPlayerToLimbo(player);

            // on change le logo du scoreboard
            ScoreboardManager.removePlayerScoreboard(player);
            ScoreboardManager.createNewScoreboard(player);

            sendFakeRespawn(player);
            System.out.println("sendFakeRespawn : " + player.getName());

            Location target = player.getLocation();

            sendEmptyChunks(player, target);
            System.out.println("sendEmptyChunks " + player.getName());
            File schemFile = new File(OMCPlugin.getInstance().getDataFolder() + "/schem", "limbo.schem");

            showStructure(schemFile, player, target, () -> {
                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                    freezePlayer(player);
                    teleportClient(player, target);
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmptyChunks(Player bukkitPlayer, Location center) {
        int chunkRadius = 1;
        ServerPlayer player = ((CraftPlayer) bukkitPlayer).getHandle();
        ServerLevel world = ((CraftWorld) center.getWorld()).getHandle();
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                System.out.println("Chunk : " + dx + ", " + dz);
                int chunkX = center.getChunk().getX() + dx;
                int chunkZ = center.getChunk().getZ() + dz;

                ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

                LevelChunk chunk = world.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) {
                    System.out.println("Chunk non chargé : " + chunkX + ", " + chunkZ);
                    continue;
                }

                ClientboundLevelChunkPacketData chunkData = new ClientboundLevelChunkPacketData(chunk);

                LevelLightEngine lightEngine = world.getChunkSource().getLightEngine();
                BitSet skyBits = new BitSet();
                BitSet blockBits = new BitSet();
                ClientboundLightUpdatePacketData lightData =
                        new ClientboundLightUpdatePacketData(chunkPos, lightEngine, skyBits, blockBits);

                PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.MAP_CHUNK);
                packet.getIntegers().write(0, chunkX);
                packet.getIntegers().write(1, chunkZ);
                packet.getSpecificModifier(ClientboundLevelChunkPacketData.class).write(0, chunkData);
                packet.getSpecificModifier(ClientboundLightUpdatePacketData.class).write(0, lightData);

                try {
                    protocolManager.sendServerPacket(bukkitPlayer, packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendFakeRespawn(Player player) {
        PacketContainer respawn = new PacketContainer(PacketType.Play.Server.RESPAWN);
        respawn.getStructures().withType(CommonPlayerSpawnInfo.class).write(0, getCommonPlayerSpawnInfo(player));

        try {
            protocolManager.sendServerPacket(player, respawn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void teleportClient(Player player, Location loc) {
        PacketContainer teleport = new PacketContainer(PacketType.Play.Server.POSITION);
        Vec3 position = new Vec3(loc.getX(), loc.getY(), loc.getZ());
        Vec3 deltaMovement = new Vec3(0, 0, 0);

        float yRot = loc.getYaw();
        float xRot = loc.getPitch();

        PositionMoveRotation posRot = new PositionMoveRotation(position, deltaMovement, yRot, xRot);

        teleport.getStructures().withType(PositionMoveRotation.class).write(0, posRot);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, teleport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exitLimbo(Player player) {
        if (playersInLimbo.contains(player.getUniqueId())) {
            applySavedInventory(player);

            removePlayerToLimbo(player);
        }
    }

    private static void sendLoginPacket(Player player) {
        PacketContainer join = protocolManager.createPacket(PacketType.Play.Server.LOGIN);

        join.getStructures().withType(CommonPlayerSpawnInfo.class).write(0, getCommonPlayerSpawnInfo(player));

        try {
            protocolManager.sendServerPacket(player, join);
        } catch (Exception e) {
            OMCPlugin.getInstance().getLogger().warning("Erreur lors de l'envoi du packet de connexion à " + player.getName() + ": " + e.getMessage());
        }
    }

    private static CommonPlayerSpawnInfo getCommonPlayerSpawnInfo(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        MinecraftServer server = nmsPlayer.server;
        ServerLevel level = server.getLevel(Level.END);

        Holder<DimensionType> dimTypeHolder = level.dimensionTypeRegistration();
        ResourceKey<Level> dimensionKey = Level.END;
        long seed = player.getWorld().getSeed();
        GameType gm = GameType.ADVENTURE;
        GameType prevGm = null;
        boolean debug = false;
        boolean flat = true;

        Optional<GlobalPos> lastDeathLocation = Optional.empty();
        int portalCooldown = 0;
        int seaLevel = level.getSeaLevel();

        return new CommonPlayerSpawnInfo(
                dimTypeHolder,
                dimensionKey,
                seed,
                gm,
                prevGm,
                debug,
                flat,
                lastDeathLocation,
                portalCooldown,
                seaLevel
        );
    }

    public static void removeFromTabList(Player player) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE);
        packet.getUUIDLists().write(0, Collections.singletonList(player.getUniqueId()));

        try {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.equals(player)) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(online, packet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void freezePlayer(Player player) {
        PacketContainer metadataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getIntegers().write(0, player.getEntityId());

        WrappedDataWatcher watcher = new WrappedDataWatcher();

        WrappedDataWatcher.WrappedDataWatcherObject flags =
                new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));

        byte bitmask = 0x08;
        watcher.setObject(flags, bitmask);

        metadataPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, metadataPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showStructure(File schemFile, Player player, Location location, Runnable whenDone) {
        Location origin = player.getLocation().clone().subtract(19, 34, 25);
        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
        if (format == null) {
            Bukkit.getLogger().severe("Format inconnu pour le fichier : " + schemFile.getName());
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
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

                                PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
                                packet.getBlockPositionModifier().write(0,
                                        new BlockPosition(target.getBlockX(), target.getBlockY(), target.getBlockZ()));
                                packet.getBlockData().write(0, WrappedBlockData.createData(blockData));
                                protocolManager.sendServerPacket(player, packet);

                            } catch (IllegalArgumentException e) {
                                Bukkit.getLogger().warning("BlockData invalide : " + blockDataString);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), whenDone);
        });
    }

    private static void saveInventory(Player player) {
        savedInventories.put(player.getUniqueId(), player.getInventory().getContents().clone());

        player.getInventory().clear();
    }

    private static void applySavedInventory(Player player) {
        ItemStack[] saved = savedInventories.remove(player.getUniqueId());
        if (saved != null) {
            player.getInventory().setContents(saved);
        }
        playersInLimbo.remove(player.getUniqueId());

        player.updateInventory();
    }
}