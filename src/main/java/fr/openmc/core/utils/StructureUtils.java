package fr.openmc.core.utils;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

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
            NBTInputStream input = null;
            try {
                input = new NBTInputStream(new FileInputStream(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Tag baseCompound = null;
            try {
                baseCompound = input.readTag();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int[] size;

            if (!baseCompound.getType().equals(TagType.TAG_COMPOUND)) return;
            CompoundMap compound = ((CompoundTag) baseCompound).getValue();

            if (!compound.containsKey("size")) return;
            Tag sizeTag = compound.get("size");
            if (!sizeTag.getType().equals(TagType.TAG_LIST)) return;
            ListTag sizeList = (ListTag) sizeTag;
            if (!(sizeList.getElementType().equals(IntTag.class)))
                return;
            if (sizeList.getValue().size() != 3) return;
            size = new int[]{((IntTag) sizeList.getValue().get(0)).getValue(), ((IntTag) sizeList.getValue().get(1)).getValue(), ((IntTag) sizeList.getValue().get(2)).getValue()};

            if (!compound.containsKey("palette")) return;
            Tag paletteTag = compound.get("palette");
            if (!paletteTag.getType().equals(TagType.TAG_LIST)) return;
            ListTag paletteList = (ListTag) paletteTag;
            if (!(paletteList.getElementType().equals(CompoundTag.class)))
                return;

            BlockData[] states = new BlockData[paletteList.getValue().size()];
            for (int stateNum = 0; stateNum < states.length; stateNum++) {
                Object oTag = paletteList.getValue().get(stateNum);
                CompoundMap blockTag = ((CompoundTag) oTag).getValue();
                StringBuilder blockStateString = new StringBuilder();

                if (!blockTag.keySet().contains("Name")) return;
                blockStateString.append(blockTag.get("Name").getValue()); //String now looks like "minecraft:log"

                if (blockTag.keySet().contains("Properties")) {
                    blockStateString.append("[");
                    if (!blockTag.get("Properties").getType().equals(TagType.TAG_COMPOUND))
                        return;
                    CompoundMap properties = ((CompoundTag) blockTag.get("Properties")).getValue();

                    Set<Map.Entry<String, Tag<?>>> properySet = properties.entrySet();
                    Iterator<Map.Entry<String, Tag<?>>> propertyIterator = properySet.iterator();
                    for (int p = 0; p < properySet.size(); p++) {
                        if (p > 0) blockStateString.append(",");
                        Tag property = propertyIterator.next().getValue();
                        blockStateString.append(property.getName()).append("=").append(property.getValue());
                    }
                    blockStateString.append("]");
                } //If the block had properties, it now looks like "minecraft:log[axis=z]

                states[stateNum] = Bukkit.createBlockData(blockStateString.toString());
            }

            if (!compound.containsKey("blocks")) return;
            Tag blocksTag = compound.get("blocks");
            if (!blocksTag.getType().equals(TagType.TAG_LIST)) return;
            ListTag blocksList = (ListTag) blocksTag;
            if (!(blocksList.getElementType().equals(CompoundTag.class)))
                return;

            List<Block> blocks = new ArrayList<>();
            for (Object oTag : blocksList.getValue()) {
                CompoundMap blockTag = ((CompoundTag) oTag).getValue();
                if (!blockTag.containsKey("pos")) return;
                Tag posTag = blockTag.get("pos");
                if (!posTag.getType().equals(TagType.TAG_LIST)) return;
                ListTag posListTag = (ListTag) posTag;
                if (!(posListTag.getElementType().equals(IntTag.class)))
                    return;
                List posList = posListTag.getValue();
                if (posListTag.getValue().size() != 3) return;
                int x = ((IntTag) posList.get(0)).getValue();
                int y = ((IntTag) posList.get(1)).getValue();
                int z = ((IntTag) posList.get(2)).getValue();

                if (mirrorX) x = (size[0] - 1) - x;
                if (mirrorZ) z = (size[2] - 1) - z;

                if (!blockTag.containsKey("state")) return;
                Tag stateTag = blockTag.get("state");
                if (!stateTag.getType().equals(TagType.TAG_INT)) return;
                IntTag stateIntTag = (IntTag) stateTag;
                if (stateIntTag.getValue() > states.length) return;
                Block block = new Location(target.getWorld(), target.getX() + x, target.getY() + y, target.getZ() + z).getBlock();

                if (!block.getType().isAir() && block.getType().isSolid()) {
                    continue;
                }

                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                    block.setBlockData(states[stateIntTag.getValue()], false);
                    blocks.add(block);
                });
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
