package fr.openmc.core.features.dream.mecanism.cold;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Player;

public class ColdManager {

    private static final NamespacedKey COLD_SPEED_KEY = new NamespacedKey(OMCPlugin.getInstance(), "cold_speed_modifier");
    private static final NamespacedKey COLD_MINING_SPEED_KEY = new NamespacedKey(OMCPlugin.getInstance(), "cold_mining_speed_modifier");

    public static void init() {
        OMCPlugin.registerEvents(
                new ColdListener()
        );
    }

    public static int calculateColdResistance(Player player) {
        return 0;
    }

    public static void applyColdEffects(Player player, int cold) {
        player.setFreezeTicks(3);

        int level = 0;
        if (cold >= 75) {
            level = 3;
        } else if (cold >= 50) {
            level = 2;
        } else if (cold >= 25) {
            level = 1;
        }

        removeColdModifier(player);

        switch (level) {
            case 1 -> {
                applySpeedModifier(player, -0.2);
                applyMiningSpeedModifier(player, -0.3);
            }
            case 2 -> {
                applySpeedModifier(player, -0.4);
                applyMiningSpeedModifier(player, -0.5);
            }
            case 3 -> {
                applySpeedModifier(player, -0.7);
                applyMiningSpeedModifier(player, -0.8);
            }
        }

        if (level > 0) {
            ParticleUtils.sendParticlePacket(player, player.getLocation().add(0, 1, 0), Particle.SPIT, 10, 0.3, 0.5, 0.3, 0.01, null);
        }
    }

    private static void applySpeedModifier(Player player, double reductionPercent) {
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed == null) return;

        AttributeModifier modifier = new AttributeModifier(COLD_SPEED_KEY, reductionPercent, AttributeModifier.Operation.ADD_SCALAR);

        speed.addModifier(modifier);
    }

    private static void applyMiningSpeedModifier(Player player, double reductionPercent) {
        AttributeInstance miningSpeed = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (miningSpeed == null) return;

        AttributeModifier modifier = new AttributeModifier(COLD_MINING_SPEED_KEY, reductionPercent, AttributeModifier.Operation.ADD_SCALAR);

        miningSpeed.addModifier(modifier);
    }

    private static void removeColdModifier(Player player) {
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        AttributeInstance miningSpeed = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (speed == null || miningSpeed == null) return;

        speed.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(COLD_SPEED_KEY))
                .findFirst()
                .ifPresent(speed::removeModifier);
        miningSpeed.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(COLD_MINING_SPEED_KEY))
                .findFirst()
                .ifPresent(miningSpeed::removeModifier);
    }

    public static boolean isNearHeatSource(Player player) {
        Location loc = player.getLocation();
        for (int x = -5; x <= 5; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -5; z <= 5; z++) {
                    Block block = loc.clone().add(x, y, z).getBlock();
                    if (block.getType() == Material.CAMPFIRE && block.getBlockData() instanceof Campfire campfire && campfire.isSignalFire())
                        return true;
                    if (block.getType() == Material.FURNACE && block.getState() instanceof Furnace furnace && furnace.getBurnTime() > 0)
                        return true;
                }
            }
        }
        return false;
    }
}
