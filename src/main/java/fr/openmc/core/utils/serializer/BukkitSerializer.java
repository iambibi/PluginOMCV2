package fr.openmc.core.utils.serializer;

import lombok.Cleanup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BukkitSerializer {
    public static byte[] serializeItemStacks(ItemStack[] inv) throws IOException {
        // TODO: Replace by ItemStack.serializeItemsAsBytes(inv)
        @Cleanup ByteArrayOutputStream os = new ByteArrayOutputStream();
        @Cleanup BukkitObjectOutputStream bos = new BukkitObjectOutputStream(os);
        bos.writeObject(inv);
        return os.toByteArray();
    }

    public static ItemStack[] deserializeItemStacks(byte[] b) throws Exception {
        // TODO: Replace by ItemStack.deserializeItemsFromBytes(b)
        @Cleanup ByteArrayInputStream bais = new ByteArrayInputStream(b);
        @Cleanup BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
        return (ItemStack[]) bois.readObject();
    }

    public static String playerInventoryToBase64(PlayerInventory inv) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(inv.getContents());
            dataOutput.writeObject(inv.getArmorContents());
            dataOutput.writeObject(inv.getExtraContents());

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save player inventory.", e);
        }
    }

    public static void playerInventoryFromBase64(PlayerInventory inv, String data) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            ItemStack[] contents = (ItemStack[]) dataInput.readObject();
            ItemStack[] armor = (ItemStack[]) dataInput.readObject();
            ItemStack[] extra = (ItemStack[]) dataInput.readObject();

            inv.setContents(contents);
            inv.setArmorContents(armor);
            inv.setExtraContents(extra);
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}