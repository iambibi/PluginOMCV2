package fr.openmc.core.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import fr.openmc.core.OMCPlugin;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

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
                if (format == null) {
                    Bukkit.getLogger().warning("Format inconnu pour " + schemFile.getName());
                    return;
                }

                Clipboard clipboard;
                try (ClipboardReader reader = format.getReader(new FileInputStream(schemFile))) {
                    clipboard = reader.read();
                }

                World weWorld = BukkitAdapter.adapt(bukkitWorld);

                try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                    var operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
                            .ignoreAirBlocks(true)
                            .build();

                    Operations.complete(operation);

                    editSession.flushSession();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
