package fr.openmc.core.features.cube;

import fr.openmc.core.OMCPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
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
}
