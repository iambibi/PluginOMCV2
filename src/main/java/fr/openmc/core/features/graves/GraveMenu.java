package fr.openmc.core.features.graves;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraveMenu extends Menu {

    private final PlayerInventory contents;

    private static final int INVENTORY_SIZE = 54;
    private static final String INVENTORY_TITLE = "§8Corps de §f";

    private static final Map<Integer, Integer> HOTBAR_SLOTS;
    private static final Map<Integer, Integer> INVENTORY_SLOTS;
    private static final Map<Integer, Integer> ARMOR_SLOTS;

    static {
        HOTBAR_SLOTS = new HashMap<>();
        HOTBAR_SLOTS.put(0, 45);
        HOTBAR_SLOTS.put(1, 46);
        HOTBAR_SLOTS.put(2, 47);
        HOTBAR_SLOTS.put(3, 48);
        HOTBAR_SLOTS.put(4, 49);
        HOTBAR_SLOTS.put(5, 50);
        HOTBAR_SLOTS.put(6, 51);
        HOTBAR_SLOTS.put(7, 52);
        HOTBAR_SLOTS.put(8, 53);

        INVENTORY_SLOTS = new HashMap<>();
        INVENTORY_SLOTS.put(27, 36);
        INVENTORY_SLOTS.put(28, 37);
        INVENTORY_SLOTS.put(29, 38);
        INVENTORY_SLOTS.put(30, 39);
        INVENTORY_SLOTS.put(31, 40);
        INVENTORY_SLOTS.put(32, 41);
        INVENTORY_SLOTS.put(33, 42);
        INVENTORY_SLOTS.put(34, 43);
        INVENTORY_SLOTS.put(35, 44);
        INVENTORY_SLOTS.put(18, 27);
        INVENTORY_SLOTS.put(19, 28);
        INVENTORY_SLOTS.put(20, 29);
        INVENTORY_SLOTS.put(21, 30);
        INVENTORY_SLOTS.put(22, 31);
        INVENTORY_SLOTS.put(23, 32);
        INVENTORY_SLOTS.put(24, 33);
        INVENTORY_SLOTS.put(25, 34);
        INVENTORY_SLOTS.put(26, 35);
        INVENTORY_SLOTS.put(9, 18);
        INVENTORY_SLOTS.put(10, 19);
        INVENTORY_SLOTS.put(11, 20);
        INVENTORY_SLOTS.put(12, 21);
        INVENTORY_SLOTS.put(13, 22);
        INVENTORY_SLOTS.put(14, 23);
        INVENTORY_SLOTS.put(15, 24);
        INVENTORY_SLOTS.put(16, 25);
        INVENTORY_SLOTS.put(17, 26);

        ARMOR_SLOTS = new HashMap<>();
        ARMOR_SLOTS.put(39, 5);  // Helmet
        ARMOR_SLOTS.put(38, 4);  // Chestplate
        ARMOR_SLOTS.put(37, 3);  // Leggings
        ARMOR_SLOTS.put(36, 2);  // Boots
        ARMOR_SLOTS.put(40, 6);  // Off-hand
    }

    public GraveMenu(Player owner, PlayerInventory contents) {
        super(owner);
        this.contents = contents;
    }

    @Override
    public @NotNull String getName() {
        return INVENTORY_TITLE + getOwner().getName();
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();
        System.out.println(Arrays.toString(contents.getContents()));
        for (ItemStack content : contents.getContents()) {
            if (content == null) continue;
            System.out.println(content.getType());
        }

        placeItems(contents, inventory, HOTBAR_SLOTS);
        placeItems(contents, inventory, INVENTORY_SLOTS);
        placeItems(contents, inventory, ARMOR_SLOTS);

        return inventory;
    }

    private void placeItems(PlayerInventory fromInventory, Map<Integer, ItemStack> inventory, Map<Integer, Integer> slotsMap) {
        for (Map.Entry<Integer, Integer> entry : slotsMap.entrySet()) {
            int fromSlot = entry.getKey();
            int toSlot = entry.getValue();

            ItemStack item = fromInventory.getItem(fromSlot);
            if (item != null) {
                inventory.put(toSlot, item);
            }
            System.out.println(inventory);
        }
        System.out.println(inventory);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
