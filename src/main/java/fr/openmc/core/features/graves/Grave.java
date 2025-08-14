package fr.openmc.core.features.graves;

import dev.lone.itemsadder.api.CustomBlock;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.entities.TextDisplay;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.PlayerInventory;
import org.joml.Vector3f;

import java.util.UUID;

@Getter
public class Grave {
    private final CustomBlock block;
    private TextDisplay textDisplay;
    private final String inventoryName;
    private final UUID ownerUUID;
    private final Location location;

    private final GraveMenu graveMenu;

    public Grave(CustomBlock block, Location location, UUID ownerUUID, PlayerInventory inventory) {
        this.block = block;
        this.location = location;
        this.ownerUUID = ownerUUID;
        this.inventoryName = "§aCorps de §6" + CacheOfflinePlayer.getOfflinePlayer(ownerUUID).getName();
        System.out.println(inventory.getContents().length);
        this.graveMenu = new GraveMenu(Bukkit.getPlayer(ownerUUID), inventory);
        createTextDisplay();
    }

    private void createTextDisplay() {
        Location loc = block.getBlock().getLocation().add(0.5, 1.5, 0.5);

        textDisplay = new TextDisplay(Component.text(inventoryName), loc, new Vector3f(0.75f));
    }

    public void remove() {
        textDisplay.remove();
        block.remove();
    }
}
