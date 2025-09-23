package fr.openmc.core.utils;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import fr.openmc.core.OMCPlugin;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StructureUtils {
    /**
     * Places a structure from an NBT file into the world at the given location.
     * Structure files can be exported using a Minecraft Structure Block.
     *
     * Optimisations :
     * - Lecture asynchrone de l’NBT, placement synchrone.
     * - Réduction des appels coûteux à world.getBlockAt() / getBlockData().
     * - Pré-calcul des positions et cache du BlockData pour palette.
     *
     * @param file    The NBT file of the structure.
     * @param target  The lowest (min corner) location where to place the structure.
     * @param mirrorX Whether to mirror the structure on the X axis (ignores block rotation).
     * @param mirrorZ Whether to mirror the structure on the Z axis (ignores block rotation).
     * @throws IOException If the NBT file is malformed or unreadable.
     */
    public static void placeStructure(File file, Location target, boolean mirrorX, boolean mirrorZ) throws IOException {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try (NBTInputStream input = new NBTInputStream(new FileInputStream(file))) {
                Tag baseCompound = input.readTag();
                if (!(baseCompound instanceof CompoundTag)) return;
                CompoundMap compound = ((CompoundTag) baseCompound).getValue();

                ListTag sizeList = (ListTag) compound.get("size");
                if (sizeList == null || sizeList.getValue().size() != 3) return;
                int[] size = new int[]{
                        ((IntTag) sizeList.getValue().get(0)).getValue(),
                        ((IntTag) sizeList.getValue().get(1)).getValue(),
                        ((IntTag) sizeList.getValue().get(2)).getValue()
                };

                ListTag paletteList = (ListTag) compound.get("palette");
                if (paletteList == null) return;
                BlockData[] states = new BlockData[paletteList.getValue().size()];
                for (int i = 0; i < states.length; i++) {
                    CompoundMap blockTag = ((CompoundTag) paletteList.getValue().get(i)).getValue();
                    StringBuilder s = new StringBuilder(blockTag.get("Name").getValue().toString());
                    if (blockTag.containsKey("Properties")) {
                        CompoundMap props = ((CompoundTag) blockTag.get("Properties")).getValue();
                        if (!props.isEmpty()) {
                            s.append("[");
                            int k = 0;
                            for (Map.Entry<String, Tag<?>> e : props.entrySet()) {
                                if (k++ > 0) s.append(",");
                                s.append(e.getKey()).append("=").append(e.getValue().getValue());
                            }
                            s.append("]");
                        }
                    }
                    states[i] = Bukkit.createBlockData(s.toString());
                }

                ListTag blocksList = (ListTag) compound.get("blocks");
                if (blocksList == null) return;
                final List<int[]> blocksToPlace = new ArrayList<>();
                final List<int[]> baseSolidCells = new ArrayList<>();

                for (Object oTag : blocksList.getValue()) {
                    CompoundMap blockTag = ((CompoundTag) oTag).getValue();
                    ListTag posListTag = (ListTag) blockTag.get("pos");
                    int x = ((IntTag) posListTag.getValue().get(0)).getValue();
                    int y = ((IntTag) posListTag.getValue().get(1)).getValue();
                    int z = ((IntTag) posListTag.getValue().get(2)).getValue();

                    if (mirrorX) x = (size[0] - 1) - x;
                    if (mirrorZ) z = (size[2] - 1) - z;

                    int stateIdx = ((IntTag) blockTag.get("state")).getValue();
                    if (stateIdx < 0 || stateIdx >= states.length) continue;

                    blocksToPlace.add(new int[]{x, y, z, stateIdx});
                    if (y == 0 && states[stateIdx].getMaterial().isSolid()) {
                        baseSolidCells.add(new int[]{x, z});
                    }
                }

                World world = target.getWorld();
                int baseX = target.getBlockX();
                int baseY = target.getBlockY();
                int baseZ = target.getBlockZ();

                int chunkMinX = (baseX) >> 4;
                int chunkMaxX = (baseX + size[0]) >> 4;
                int chunkMinZ = (baseZ) >> 4;
                int chunkMaxZ = (baseZ + size[2]) >> 4;

                List<CompletableFuture<Chunk>> futures = new ArrayList<>();
                for (int cx = chunkMinX; cx <= chunkMaxX; cx++) {
                    for (int cz = chunkMinZ; cz <= chunkMaxZ; cz++) {
                        futures.add(world.getChunkAtAsync(cx, cz, true));
                    }
                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                        ServerLevel handle = ((CraftWorld) world).getHandle();

                        int floating = 0;
                        int checked = 0;
                        for (int i = 0; i < baseSolidCells.size(); i += 3) {
                            int[] rc = baseSolidCells.get(i);
                            BlockPos pos = new BlockPos(baseX + rc[0], baseY - 1, baseZ + rc[1]);
                            net.minecraft.world.level.block.state.BlockState state = handle.getBlockState(pos);
                            if (state.isAir() || !state.getBukkitMaterial().isSolid()) {
                                floating++;
                            }
                            checked++;
                        }

                        if (checked > 0 && ((double) floating / checked) > 0.40D) {
                            return;
                        }

                        final int batchSize = 500;
                        new BukkitRunnable() {
                            int index = 0;

                            @Override
                            public void run() {
                                int placed = 0;
                                while (index < blocksToPlace.size() && placed < batchSize) {
                                    int[] e = blocksToPlace.get(index++);
                                    BlockPos pos = new BlockPos(baseX + e[0], baseY + e[1], baseZ + e[2]);
                                    BlockData data = states[e[3]];

                                    if (data.getMaterial().equals(Material.AIR)) continue;
                                    if (data.getMaterial().equals(Material.STRUCTURE_VOID)) continue;

                                    handle.setBlock(pos, ((CraftBlockData) data).getState(), 2 | 16);
                                    placed++;
                                }
                                if (index >= blocksToPlace.size()) {
                                    cancel();
                                }
                            }
                        }.runTaskTimer(OMCPlugin.getInstance(), 1L, 1L);
                    });
                });

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * Permet de récupérer un fichier de structure à partir de son nom
     *
     * @param name Nom du fichier
     * @return Fichier de structure
     */
    public static File getStructureFile(String group, String name) {
        name = name.replace(".nbt", "");
        String relativePath = "structures/" + group + "/" + name + ".nbt";

        File destFile = new File(OMCPlugin.getInstance().getDataFolder(), relativePath);

        if (!destFile.exists()) {
            destFile.getParentFile().mkdirs();

            try (InputStream in = OMCPlugin.getInstance().getResource(relativePath)) {
                if (in == null) {
                    throw new IllegalArgumentException("Structure introuvable dans resources: " + relativePath);
                }

                try (OutputStream out = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Impossible de copier la structure: " + relativePath, e);
            }
        }

        return destFile;
    }
}
