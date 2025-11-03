package fr.openmc.core.features.dream.registries.mobs;

import fr.openmc.core.features.dream.models.registry.DreamMob;
import fr.openmc.core.features.dream.registries.DreamMobsRegistry;
import fr.openmc.core.utils.RandomUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tadpole;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CorruptedTadpole extends DreamMob implements Listener {

    public CorruptedTadpole() {
        super("corrupted_tadpole",
                "Tétard Corrompu",
                EntityType.TADPOLE,
                25.0,
                0.0,
                RandomUtils.randomBetween(0.2, 0.4),
                RandomUtils.randomBetween(5, 6.3),
                List.of()
        );
    }

    @Override
    public LivingEntity spawn(Location location) {
        Tadpole tadpole = (Tadpole) location.getWorld().spawnEntity(location.add(0, 1, 0), this.getType(), CreatureSpawnEvent.SpawnReason.CUSTOM);

        tadpole.customName(Component.text(this.getName()));
        tadpole.setCustomNameVisible(true);

        this.setAttributeIfPresent(tadpole, Attribute.MAX_HEALTH, this.getHealth());
        tadpole.setHealth(this.getHealth());
        this.setAttributeIfPresent(tadpole, Attribute.ATTACK_DAMAGE, this.getDamage());
        this.setAttributeIfPresent(tadpole, Attribute.MOVEMENT_SPEED, this.getSpeed());
        this.setAttributeIfPresent(tadpole, Attribute.SCALE, this.getScale());

        tadpole.getPersistentDataContainer().set(
                DreamMobsRegistry.mobKey,
                PersistentDataType.STRING,
                this.getId()
        );

        return tadpole;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!DreamMobsRegistry.isDreamMob(entity)) return;

        DreamMob mob = DreamMobsRegistry.getFromEntity(entity);
        if (mob == null) return;
        if (!mob.getId().equals(this.getId())) return;

        DreamMob crazyFrog = DreamMobsRegistry.getByName("crazy_frog");

        if (crazyFrog == null) return;
        crazyFrog.spawn(entity.getLocation());
    }
}