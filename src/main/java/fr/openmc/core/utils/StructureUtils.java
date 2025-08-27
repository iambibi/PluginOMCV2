package fr.openmc.core.utils;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                    World world = target.getWorld();
                    int baseX = target.getBlockX();
                    int baseY = target.getBlockY();
                    int baseZ = target.getBlockZ();

                    int floating = 0;
                    for (int[] rc : baseSolidCells) {
                        Material below = world.getBlockAt(baseX + rc[0], baseY - 1, baseZ + rc[1]).getType();
                        if (below.isAir() || !below.isSolid()) floating++;
                    }
                    if (!baseSolidCells.isEmpty() && ((double) floating / baseSolidCells.size()) > 0.40D) {
                        return;
                    }

                    for (int[] e : blocksToPlace) {
                        int rx = e[0], ry = e[1], rz = e[2], sIdx = e[3];
                        Block block = world.getBlockAt(baseX + rx, baseY + ry, baseZ + rz);
                        if (block.getType().isSolid() && !block.getType().isAir()) continue;
                        block.setBlockData(states[sIdx], false);
                    }
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
        return new File("world/generated/" + group + "/structures/" + name + ".nbt");
    }
}
