package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.models.registry.DreamLoot;
import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import fr.openmc.core.utils.RandomUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CrazyFrog extends DreamMob {

    public CrazyFrog() {
        super("crazy_frog",
                "Grenouille Folle",
                EntityType.FROG,
                18.0,
                0.0,
                RandomUtils.randomBetween(0.2, 0.4),
                RandomUtils.randomBetween(3, 2.3),
                List.of(new DreamLoot(
                        DreamItemRegistry.getByName("omc_dream:metal_detector"),
                        0.5,
                        1,
                        1
                ))
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        Frog frog = (Frog) location.getWorld().spawnEntity(location.add(0, 1, 0), this.getType(), CreatureSpawnEvent.SpawnReason.CUSTOM);

        frog.setVariant(Frog.Variant.WARM);
        frog.setVelocity(location.getDirection().multiply(1.3));
        frog.customName(Component.text(this.getName()));
        frog.setCustomNameVisible(true);

        this.setAttributeIfPresent(frog, Attribute.MAX_HEALTH, this.getHealth());
        frog.setHealth(this.getHealth());
        this.setAttributeIfPresent(frog, Attribute.ATTACK_DAMAGE, this.getDamage());
        this.setAttributeIfPresent(frog, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(frog, Attribute.SCALE, this.getScale());

        frog.setPersistent(true);

        frog.getPersistentDataContainer().set(
                DreamMobsRegistry.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );

        return frog;
    }
}
