package fr.openmc.core.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ColorUtils {

    private ColorUtils() {
        throw new IllegalStateException("For Sonar");
    }

    private static final Map<NamedTextColor, NamedTextColor> colorToReadable = new HashMap<>();
    static {
        colorToReadable.put(BLACK, DARK_GRAY);
        colorToReadable.put(DARK_BLUE, DARK_BLUE);
        colorToReadable.put(DARK_GREEN, DARK_GREEN);
        colorToReadable.put(DARK_AQUA, DARK_AQUA);
        colorToReadable.put(DARK_RED, DARK_RED);
        colorToReadable.put(DARK_PURPLE, DARK_PURPLE);
        colorToReadable.put(GOLD, NamedTextColor.GOLD);
        colorToReadable.put(GRAY, NamedTextColor.GRAY);
        colorToReadable.put(DARK_GRAY, NamedTextColor.DARK_GRAY);
        colorToReadable.put(BLUE, NamedTextColor.BLUE);
        colorToReadable.put(NamedTextColor.GREEN, NamedTextColor.GREEN);
        colorToReadable.put(NamedTextColor.AQUA, NamedTextColor.AQUA);
        colorToReadable.put(NamedTextColor.RED, NamedTextColor.RED);
        colorToReadable.put(NamedTextColor.LIGHT_PURPLE, NamedTextColor.LIGHT_PURPLE);
        colorToReadable.put(NamedTextColor.YELLOW, NamedTextColor.GOLD);
        colorToReadable.put(NamedTextColor.WHITE, NamedTextColor.GRAY);
    }

    /**
     * Retourne une couleur plus visible sur les Livres (blanc sur blanc ça se voit pas)
     */
    public static NamedTextColor getReadableColor(NamedTextColor c) {
        return colorToReadable.getOrDefault(c, null);
    }

    private static final Map<NamedTextColor, Material> colorToMaterial = new HashMap<>();
    static {
        colorToMaterial.put(BLACK, Material.BLACK_WOOL);
        colorToMaterial.put(DARK_BLUE, Material.BLUE_WOOL);
        colorToMaterial.put(NamedTextColor.DARK_GREEN, Material.GREEN_WOOL);
        colorToMaterial.put(NamedTextColor.DARK_AQUA, Material.CYAN_WOOL);
        colorToMaterial.put(NamedTextColor.DARK_RED, Material.RED_WOOL);
        colorToMaterial.put(NamedTextColor.DARK_PURPLE, Material.PURPLE_WOOL);
        colorToMaterial.put(NamedTextColor.GOLD, Material.ORANGE_WOOL);
        colorToMaterial.put(NamedTextColor.GRAY, Material.LIGHT_GRAY_WOOL);
        colorToMaterial.put(NamedTextColor.DARK_GRAY, Material.GRAY_WOOL);
        colorToMaterial.put(NamedTextColor.BLUE, Material.LIGHT_BLUE_WOOL);
        colorToMaterial.put(NamedTextColor.GREEN, Material.LIME_WOOL);
        colorToMaterial.put(NamedTextColor.AQUA, Material.CYAN_WOOL);
        colorToMaterial.put(NamedTextColor.RED, Material.RED_WOOL);
        colorToMaterial.put(NamedTextColor.LIGHT_PURPLE, Material.MAGENTA_WOOL);
        colorToMaterial.put(NamedTextColor.YELLOW, Material.YELLOW_WOOL);
        colorToMaterial.put(NamedTextColor.WHITE, Material.WHITE_WOOL);
    }

    /**
     * Retourne une laine de couleur en fonction de la couleur rentré
     */
    public static Material getMaterialFromColor(NamedTextColor c) {
        return colorToMaterial.getOrDefault(c, null);
    }

    /**
     * Retourne une couleur en fonction du String (LIGHT_PURPLE => NamedTextColor.LIGHT_PURPLE)
     */
    public static NamedTextColor getNamedTextColor(String color) {
        if (color == null) {
            return NamedTextColor.WHITE;
        }
        return NamedTextColor.NAMES.valueOr(color.toLowerCase(), NamedTextColor.WHITE);
    }

    private static final Map<NamedTextColor, String> colorToName = new HashMap<>();
    static {
        colorToName.put(BLACK, "§0Noir");
        colorToName.put(DARK_BLUE, "§1Bleu Foncé");
        colorToName.put(NamedTextColor.DARK_GREEN, "§2Vert Foncé");
        colorToName.put(NamedTextColor.DARK_AQUA, "§3Aqua Foncé");
        colorToName.put(NamedTextColor.DARK_RED, "§4Rouge Foncé");
        colorToName.put(NamedTextColor.DARK_PURPLE, "§5Violet");
        colorToName.put(NamedTextColor.GOLD, "§6Orange");
        colorToName.put(NamedTextColor.GRAY, "§7Gris");
        colorToName.put(NamedTextColor.DARK_GRAY, "§8Gris Foncé");
        colorToName.put(NamedTextColor.BLUE, "§9Bleu");
        colorToName.put(NamedTextColor.GREEN, "§aVert Clair");
        colorToName.put(NamedTextColor.AQUA, "§bBleu Clair");
        colorToName.put(NamedTextColor.RED, "§cRouge");
        colorToName.put(NamedTextColor.LIGHT_PURPLE, "§dRose");
        colorToName.put(NamedTextColor.YELLOW, "§eJaune");
        colorToName.put(NamedTextColor.WHITE, "§fBlanc");
    }

    /**
     * Retourne un String qui contient la couleur rentré
     */
    public static String getNameFromColor(NamedTextColor c) {
        return colorToName.getOrDefault(c, "Aucun");
    }

    private static final Map<NamedTextColor, String> colorCode = new HashMap<>();
    static {
        colorCode.put(BLACK, "§0");
        colorCode.put(DARK_BLUE, "§1");
        colorCode.put(NamedTextColor.DARK_GREEN, "§2");
        colorCode.put(NamedTextColor.DARK_AQUA, "§3");
        colorCode.put(NamedTextColor.DARK_RED, "§4");
        colorCode.put(NamedTextColor.DARK_PURPLE, "§5");
        colorCode.put(NamedTextColor.GOLD, "§6");
        colorCode.put(NamedTextColor.GRAY, "§7");
        colorCode.put(NamedTextColor.DARK_GRAY, "§8");
        colorCode.put(NamedTextColor.BLUE, "§9");
        colorCode.put(NamedTextColor.GREEN, "§a");
        colorCode.put(NamedTextColor.AQUA, "§b");
        colorCode.put(NamedTextColor.RED, "§c");
        colorCode.put(NamedTextColor.LIGHT_PURPLE, "§d");
        colorCode.put(NamedTextColor.YELLOW, "§e");
        colorCode.put(NamedTextColor.WHITE, "§f");
    };

    /**
     * Retourne un code couleur en § en fonction de la couleur donnée
     */
    public static String getColorCode(NamedTextColor color) {
        return colorCode.getOrDefault(color, "§f");
    }

    private static final Map<NamedTextColor, int[]> COLOR_RGB_MAP = Map.ofEntries(
            Map.entry(NamedTextColor.BLACK, new int[]{0, 0, 0}),
            Map.entry(NamedTextColor.DARK_BLUE, new int[]{0, 0, 170}),
            Map.entry(NamedTextColor.DARK_GREEN, new int[]{0, 170, 0}),
            Map.entry(NamedTextColor.DARK_AQUA, new int[]{0, 170, 170}),
            Map.entry(NamedTextColor.DARK_RED, new int[]{170, 0, 0}),
            Map.entry(NamedTextColor.DARK_PURPLE, new int[]{170, 0, 170}),
            Map.entry(NamedTextColor.GOLD, new int[]{255, 170, 0}),
            Map.entry(NamedTextColor.GRAY, new int[]{170, 170, 170}),
            Map.entry(NamedTextColor.DARK_GRAY, new int[]{85, 85, 85}),
            Map.entry(NamedTextColor.BLUE, new int[]{85, 85, 255}),
            Map.entry(NamedTextColor.GREEN, new int[]{85, 255, 85}),
            Map.entry(NamedTextColor.AQUA, new int[]{85, 255, 255}),
            Map.entry(NamedTextColor.RED, new int[]{255, 85, 85}),
            Map.entry(NamedTextColor.LIGHT_PURPLE, new int[]{255, 85, 255}),
            Map.entry(NamedTextColor.YELLOW, new int[]{255, 255, 85}),
            Map.entry(NamedTextColor.WHITE, new int[]{255, 255, 255})
    );

    public static int[] getRGBFromNamedTextColor(NamedTextColor color) {
        return COLOR_RGB_MAP.getOrDefault(color, new int[]{255, 255, 255});
    }
}
