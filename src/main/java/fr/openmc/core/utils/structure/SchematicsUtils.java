package fr.openmc.core.utils.structure;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SchematicsUtils {

    /**
     * /!\ must be put in ressources folder
     *
     * @param nameSchem Just name of file ex. limbo without .schem
     */
    public static void extractSchematic(String nameSchem) {
        OMCPlugin plugin = OMCPlugin.getInstance();
        File schemFolder = new File(plugin.getDataFolder(), "schem");
        if (!schemFolder.exists()) {
            schemFolder.mkdirs();
        }

        File outFile = new File(schemFolder, nameSchem + ".schem");
        if (!outFile.exists()) {
            try (InputStream in = plugin.getResource("schem/" + nameSchem + ".schem")) {
                if (in == null) {
                    plugin.getLogger().warning("Le fichier '" + nameSchem + ".schem' est introuvable dans les ressources.");
                    return;
                }

                Files.copy(in, outFile.toPath());
                plugin.getLogger().info("Fichier '" + nameSchem + ".schem' extrait dans plugins/OpenMC/schem/.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void pasteSchem(org.bukkit.World bukkitWorld, File schemFile, org.bukkit.Location loc) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                var format = ClipboardFormats.findByFile(schemFile);
                if (format == null) return;

                Clipboard clipboard;
                try (ClipboardReader reader = format.getReader(new FileInputStream(schemFile))) {
                    clipboard = reader.read();
                }

                Region region = clipboard.getRegion();
                BlockVector3 min = region.getMinimumPoint();

                List<BlockVector3> baseSolidCells = new ArrayList<>();

                for (BlockVector3 pos : region) {
                    if (pos.y() == min.y()) {
                        BlockState block = clipboard.getBlock(pos);
                        if (block.getBlockType().getMaterial().isSolid()) {
                            baseSolidCells.add(pos.subtract(min));
                        }
                    }
                }

                int baseX = loc.getBlockX();
                int baseY = loc.getBlockY();
                int baseZ = loc.getBlockZ();

                int floating = 0;
                int checked = 0;

                for (int i = 0; i < baseSolidCells.size(); i += 3) {
                    BlockVector3 rel = baseSolidCells.get(i);
                    int worldX = baseX + rel.x();
                    int worldZ = baseZ + rel.z();

                    if (baseY - 1 < bukkitWorld.getMinHeight()) {
                        floating++;
                        checked++;
                        continue;
                    }

                    Material below = bukkitWorld.getBlockAt(worldX, baseY - 1, worldZ).getType();
                    if (below.isAir() || !below.isSolid()) {
                        floating++;
                    }
                    checked++;
                }

                if (checked > 0 && ((double) floating / checked) > 0.40D) return;

                World weWorld = BukkitAdapter.adapt(bukkitWorld);
                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                    try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                        var operation = new ClipboardHolder(clipboard)
                                .createPaste(editSession)
                                .to(BlockVector3.at(baseX, baseY, baseZ))
                                .ignoreAirBlocks(true)
                                .build();

                        Operations.complete(operation);
                        editSession.flushSession();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
