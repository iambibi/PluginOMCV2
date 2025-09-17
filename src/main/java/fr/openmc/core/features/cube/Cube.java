package fr.openmc.core.features.cube;

import fr.openmc.core.OMCPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

// Les Restes du Cube. Aucun mouvement possible, juste pour le lore, les souvenirs, l'easter egg, bref :)
// - iambibi_
public class Cube extends MultiBlock {
    public Cube(Location origin, int size, Material material) {
        super(origin, size, material);
    }

    @Override
    public void build() {
        World world = this.origin.getWorld();
        int baseX = this.origin.getBlockX();
        int baseY = this.origin.getBlockY();
        int baseZ = this.origin.getBlockZ();

        for (int x = 0; x < this.radius; x++) {
            for (int y = 0; y < this.radius; y++) {
                for (int z = 0; z < this.radius; z++) {
                    Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
                    block.setType(material);
                }
            }
        }
    }

    @Override
    public void clear() {
        World world = this.origin.getWorld();
        int baseX = this.origin.getBlockX();
        int baseY = this.origin.getBlockY();
        int baseZ = this.origin.getBlockZ();

        for (int x = 0; x < this.radius; x++) {
            for (int y = 0; y < this.radius; y++) {
                for (int z = 0; z < this.radius; z++) {
                    Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
                    if (block.getType() == this.material) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    @Override
    public boolean isPartOf(Location loc) {
        if (loc == null) return false;
        if (loc.getBlock().getType().equals(Material.AIR)) return false;
        if (!loc.getBlock().getType().equals(this.material)) return false;

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int baseX = this.origin.getBlockX();
        int baseY = this.origin.getBlockY();
        int baseZ = this.origin.getBlockZ();

        return x >= baseX && x < baseX + this.radius &&
                y >= baseY && y < baseY + this.radius &&
                z >= baseZ && z < baseZ + this.radius &&
                loc.getBlock().getType() == this.material;
    }

    public Location getCenter() {
        return this.origin.clone().add(this.radius / 2.0, this.radius / 2.0, this.radius / 2.0);
    }

    public void repulsePlayer(Player player, boolean isOnCube) {
        Vector velocity = isOnCube ? player.getVelocity() :
                player.getLocation().toVector().subtract(this.getCenter().toVector()).normalize();

        velocity.setY(1);


        if (!isOnCube) {
            velocity.multiply(3);
        }

        player.setVelocity(velocity);

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (!RepulseEffectListener.noFallPlayers.contains(player.getUniqueId())) {
                RepulseEffectListener.noFallPlayers.add(player.getUniqueId());
                RepulseEffectListener.startNoFallParticles(player);
            }
        }, 2L);

        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 2.0f);
    }

    public void startMagneticShock() {
        World world = this.origin.getWorld();

        world.strikeLightningEffect(this.getCenter());

        int shockRadius = this.radius * 4;

        Location center = this.getCenter();
        int rays = 60;
        for (int i = 0; i < rays; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.acos(2 * Math.random() - 1);
            Vector dir = new Vector(
                    Math.sin(phi) * Math.cos(theta),
                    Math.cos(phi),
                    Math.sin(phi) * Math.sin(theta)
            ).normalize();

            for (int j = 1; j <= shockRadius; j++) {
                Location point = center.clone().add(dir.clone().multiply(j));
                world.spawnParticle(
                        Particle.ELECTRIC_SPARK,
                        point,
                        2,
                        0.05, 0.05, 0.05,
                        0.01
                );
            }
        }

        for (int x = -shockRadius; x <= shockRadius; x++) {
            for (int y = -shockRadius; y <= shockRadius; y++) {
                for (int z = -shockRadius; z <= shockRadius; z++) {
                    Location loc = this.getCenter().clone().add(x, y, z);
                    Block block = loc.getBlock();
                    BlockData data = block.getBlockData();

                    if (data instanceof Powerable powerable) {
                        powerable.setPowered(!powerable.isPowered());
                        block.setBlockData(powerable, true);

                        world.spawnParticle(Particle.ELECTRIC_SPARK, loc.add(0.5, 0.5, 0.5), 8,
                                0.2, 0.2, 0.2
                        );
                    }

                    if (data instanceof Lightable lightable && data instanceof Powerable) {
                        lightable.setLit(!lightable.isLit());
                        block.setBlockData(lightable, true);

                        world.spawnParticle(Particle.ENCHANT, loc.add(0.5, 0.5, 0.5), 8,
                                0.2, 0.2, 0.2
                        );
                    }
                }
            }
        }

        for (Player player : world.getPlayers()) {
            if (player.getLocation().distance(this.getCenter()) <= shockRadius) {
                world.spawnParticle(Particle.FLASH, player.getLocation().add(0, 1, 0), 5, 0.3, 0.5, 0.3, 0.01);
                world.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
            } else {
                world.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.2f, 2f);
            }
        }
    }
}
