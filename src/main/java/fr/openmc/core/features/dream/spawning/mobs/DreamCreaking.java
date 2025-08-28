package fr.openmc.core.features.dream.spawning.mobs;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.spawning.DreamMob;
import fr.openmc.core.features.dream.spawning.DreamSpawningManager;
import fr.openmc.core.utils.RandomUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creaking;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

public class DreamCreaking extends DreamMob {

    public DreamCreaking() {
        super("Creaking Insomiaque",
                EntityType.CREAKING,
                0,
                6.0,
                RandomUtils.randomBetween(0.4, 0.6),
                RandomUtils.randomBetween(1.2, 1.7),
                DreamBiome.SCULK_PLAINS.getBiome()
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        return null;
    }

    public void apply(Creaking creaking) {
        creaking.customName(Component.text(this.getName()));
        creaking.setCustomNameVisible(true);

        this.setAttributeIfPresent(creaking, Attribute.ATTACK_DAMAGE, this.getDamage());
        this.setAttributeIfPresent(creaking, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(creaking, Attribute.SCALE, this.getScale());

        creaking.getPersistentDataContainer().set(
                DreamSpawningManager.mobKey,
                PersistentDataType.BYTE,
                (byte) 1
        );
    }
}