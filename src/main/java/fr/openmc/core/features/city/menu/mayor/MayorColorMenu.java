package fr.openmc.core.features.city.menu.mayor;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.MayorElector;
import fr.openmc.core.features.city.mayor.PerkType;
import fr.openmc.core.features.city.mayor.Perks;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.menu.ConfirmMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MayorColorMenu extends Menu {
    private final Perks perk2;
    private final Perks perk3;

    public MayorColorMenu(Player owner, Perks perk2, Perks perk3) {
        super(owner);
        this.perk2 = perk2;
        this.perk3 = perk3;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();
        City city = CityManager.getPlayerCity(player.getUniqueId());

        Map<NamedTextColor, Integer> colorSlot = new HashMap<>();
        {
            colorSlot.put(NamedTextColor.RED, 3);
            colorSlot.put(NamedTextColor.GOLD, 4);
            colorSlot.put(NamedTextColor.YELLOW, 5);
            colorSlot.put(NamedTextColor.GREEN, 10);
            colorSlot.put(NamedTextColor.DARK_GREEN, 11);
            colorSlot.put(NamedTextColor.BLUE, 12);
            colorSlot.put(NamedTextColor.AQUA, 13);
            colorSlot.put(NamedTextColor.DARK_BLUE, 14);
            colorSlot.put(NamedTextColor.DARK_PURPLE, 15);
            colorSlot.put(NamedTextColor.LIGHT_PURPLE, 16);
            colorSlot.put(NamedTextColor.WHITE, 21);
            colorSlot.put(NamedTextColor.GRAY, 22);
            colorSlot.put(NamedTextColor.DARK_GRAY, 23);
        }
        colorSlot.forEach((color, slot) -> {
            List<Component> loreColor = List.of(
                    Component.text("§7Votre nom sera affiché en " + ColorUtils.getNameFromColor(color)),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            );
            inventory.put(slot, new ItemBuilder(this, ColorUtils.getMaterialFromColor(color), itemMeta -> {
                itemMeta.displayName(Component.text("§7Mettez du " + ColorUtils.getNameFromColor(color)));
                itemMeta.lore(loreColor);
            }).setOnClick(inventoryClickEvent -> {
                List<Component> loreAccept = new ArrayList<>(List.of(
                        Component.text("§7Vous allez vous présenter en tant que §6Maire de " + city.getName()),
                        Component.text(""),
                        Component.text("Maire " + player.getName()).color(color).decoration(TextDecoration.ITALIC, false)
                ));
                loreAccept.add(Component.text(perk2.getName()));
                loreAccept.addAll(perk2.getLore());
                loreAccept.add(Component.text(""));
                loreAccept.add(Component.text(perk3.getName()));
                loreAccept.addAll(perk3.getLore());
                loreAccept.add(Component.text(""));
                loreAccept.add(Component.text("§c§lAUCUN RETOUR EN ARRIERE POSSIBLE!"));


                ConfirmMenu menu = new ConfirmMenu(player,
                        () -> {
                            MayorElector elector = new MayorElector(city, player.getName(), player.getUniqueId().toString(), color, perk2.getId(), perk3.getId(), 0);
                            MayorManager.getInstance().createElector(city, elector);
                            MessagesManager.sendMessage(player, Component.text("§7Vous vous êtes présenter avec §asuccès§7!"), Prefix.CITY, MessageType.ERROR, false);
                            player.closeInventory();
                        },
                        () -> {
                            player.closeInventory();
                        },
                        loreAccept,
                        List.of(
                                Component.text("§7Ne pas se présenter en tant que §6Maire de " + city.getName())
                        )
                );
                menu.open();
            }));
        });


        return inventory;
    }
}