package fr.openmc.core.features.dream.spawning.mobs;

import fr.openmc.core.features.dream.generation.DreamBiome;
import fr.openmc.core.features.dream.spawning.DreamMob;
import fr.openmc.core.features.dream.spawning.DreamSpawningManager;
import fr.openmc.core.utils.RandomUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

public class DreamSpider extends DreamMob {

    public DreamSpider() {
        super("Arraignée Infestée",
                EntityType.SPIDER,
                40.0,
                6.0,
                RandomUtils.randomBetween(0.6, 0.8),
                RandomUtils.randomBetween(1.5, 2.0),
                DreamBiome.SCULK_PLAINS.getBiome()
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        Spider spider = (Spider) location.getWorld().spawnEntity(location, this.getType(), CreatureSpawnEvent.SpawnReason.CUSTOM);

        spider.customName(Component.text(this.getName()));
        spider.setCustomNameVisible(true);

        this.setAttributeIfPresent(spider, Attribute.MAX_HEALTH, this.getHealth());
        spider.setHealth(this.getHealth());
        this.setAttributeIfPresent(spider, Attribute.ATTACK_DAMAGE, this.getDamage());
        this.setAttributeIfPresent(spider, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(spider, Attribute.SCALE, this.getScale());

        spider.setPersistent(true);

        spider.getPersistentDataContainer().set(
                DreamSpawningManager.mobKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        return spider;
    }
}