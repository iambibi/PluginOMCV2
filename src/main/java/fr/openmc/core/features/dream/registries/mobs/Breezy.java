package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Breeze;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Breezy extends DreamMob {

    public Breezy() {
        super("brezzy",
                "Breezy",
                EntityType.BREEZE,
                100.0,
                8.0,
                0.7,
                4.0,
                List.of()
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        return null;
    }

    public EntitySnapshot createSnapshot() {
        World world = Bukkit.getWorld(DreamDimensionManager.DIMENSION_NAME);
        if (world == null) return null;
        Breeze breeze = world.createEntity(new Location(world, 0, 0, 0), Breeze.class);

        applyStats(breeze);

        breeze.addPotionEffect(new PotionEffect(
                PotionEffectType.INFESTED,
                Integer.MAX_VALUE,
                0,
                false,
                true
        ));
        breeze.setGlowing(true);
        breeze.setLootTable(null);

        return breeze.createSnapshot();
    }
}