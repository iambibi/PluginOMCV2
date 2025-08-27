package fr.openmc.core.utils;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StructureUtils {
    /**
     * The function that places a structure into a world from an NBT file. You can generate this file using the in-game
     * <a href="https://minecraft.gamepedia.com/Structure_Block">MC Structure Block docs</a>
     * Credit : <a href="https://github.com/rodiconmc/Rodiblock/blob/master/src/main/java/com/rodiconmc/rodiblock/Structure.java">rodiconmc</a>
     *
     * @param file    NBT file of the structure
     * @param target  The lowest value location of where you want to place the structure.
     * @param mirrorX Whether or not to mirror the structure on the X coordinate. (Does not change block rotation values)
     * @param mirrorZ Whether or not to mirror the structure on the Z coordinate. (Does not change block rotation values)
     * @throws IOException Exception thrown if the NBT file is formatted incorrectly.
     */
    public static void placeStructure(File file, Location target, boolean mirrorX, boolean mirrorZ) throws IOException {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try (NBTInputStream input = new NBTInputStream(new FileInputStream(file))) {
                Tag baseCompound = input.readTag();
                if (!baseCompound.getType().equals(TagType.TAG_COMPOUND)) return;
                CompoundMap compound = ((CompoundTag) baseCompound).getValue();

                if (!compound.containsKey("size")) return;
                ListTag sizeList = (ListTag) compound.get("size");
                if (!(sizeList.getElementType().equals(IntTag.class)) || sizeList.getValue().size() != 3) return;
                int[] size = new int[]{
                        ((IntTag) sizeList.getValue().get(0)).getValue(),
                        ((IntTag) sizeList.getValue().get(1)).getValue(),
                        ((IntTag) sizeList.getValue().get(2)).getValue()
                };

                if (!compound.containsKey("palette")) return;
                ListTag paletteList = (ListTag) compound.get("palette");
                if (!(paletteList.getElementType().equals(CompoundTag.class))) return;

                BlockData[] states = new BlockData[paletteList.getValue().size()];
                for (int i = 0; i < states.length; i++) {
                    CompoundMap blockTag = ((CompoundTag) paletteList.getValue().get(i)).getValue();
                    if (!blockTag.containsKey("Name")) return;

                    StringBuilder s = new StringBuilder(blockTag.get("Name").getValue().toString()); // e.g. "minecraft:oak_log"
                    if (blockTag.containsKey("Properties")) {
                        CompoundMap props = ((CompoundTag) blockTag.get("Properties")).getValue();
                        Iterator<Map.Entry<String, Tag<?>>> it = props.entrySet().iterator();
                        if (!props.isEmpty()) s.append("[");
                        int k = 0;
                        while (it.hasNext()) {
                            Map.Entry<String, Tag<?>> e = it.next();
                            if (k++ > 0) s.append(",");
                            s.append(e.getKey()).append("=").append(e.getValue().getValue());
                        }
                        if (!props.isEmpty()) s.append("]");
                    }
                    states[i] = Bukkit.createBlockData(s.toString());
                }

                if (!compound.containsKey("blocks")) return;
                ListTag blocksList = (ListTag) compound.get("blocks");
                if (!(blocksList.getElementType().equals(CompoundTag.class))) return;

                final List<int[]> blocksToPlace = new ArrayList<>(); // {x,y,z,stateIdx}
                final List<int[]> baseSolidCells = new ArrayList<>(); // {x,z} pour y==0 et block SOLIDE

                for (Object oTag : blocksList.getValue()) {
                    CompoundMap blockTag = ((CompoundTag) oTag).getValue();

                    ListTag posListTag = (ListTag) blockTag.get("pos");
                    if (!(posListTag.getElementType().equals(IntTag.class)) || posListTag.getValue().size() != 3)
                        return;
                    int x = ((IntTag) posListTag.getValue().get(0)).getValue();
                    int y = ((IntTag) posListTag.getValue().get(1)).getValue();
                    int z = ((IntTag) posListTag.getValue().get(2)).getValue();

                    if (mirrorX) x = (size[0] - 1) - x;
                    if (mirrorZ) z = (size[2] - 1) - z;

                    IntTag stateIntTag = (IntTag) blockTag.get("state");
                    int stateIdx = stateIntTag.getValue();
                    if (stateIdx < 0 || stateIdx >= states.length) return;

                    blocksToPlace.add(new int[]{x, y, z, stateIdx});

                    if (y == 0 && states[stateIdx].getMaterial().isSolid()) {
                        baseSolidCells.add(new int[]{x, z});
                    }
                }

                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                    World world = target.getWorld();
                    int baseY = target.getBlockY();
                    int baseX = target.getBlockX();
                    int baseZ = target.getBlockZ();

                    int totalBaseSolid = baseSolidCells.size();
                    int floating = 0;
                    for (int[] rc : baseSolidCells) {
                        Block below = world.getBlockAt(baseX + rc[0], baseY - 1, baseZ + rc[1]);
                        if (below.isEmpty() || !below.getType().isSolid()) {
                            floating++;
                        }
                    }
                    if (totalBaseSolid > 0) {
                        double ratio = (double) floating / (double) totalBaseSolid;
                        if (ratio > 0.40D) {
                            return;
                        }
                    }

                    for (int[] e : blocksToPlace) {
                        int rx = e[0], ry = e[1], rz = e[2], sIdx = e[3];
                        Block worldBlock = world.getBlockAt(baseX + rx, baseY + ry, baseZ + rz);

                        if (!worldBlock.getType().isAir() && worldBlock.getType().isSolid()) continue;

                        worldBlock.setBlockData(states[sIdx], false);
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
