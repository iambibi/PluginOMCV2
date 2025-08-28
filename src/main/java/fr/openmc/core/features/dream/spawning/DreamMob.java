package fr.openmc.core.features.dream.spawning;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

@Getter
public abstract class DreamMob {
    private final String name;
    private final EntityType type;
    private final double health;
    private final double damage;
    private final double speed;
    private final double scale;
    private final Biome[] spawnBiomes;

    public DreamMob(String name, EntityType type, double health, double damage, double speed, double scale, Biome... biomes) {
        this.name = name;
        this.type = type;
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.scale = scale;
        this.spawnBiomes = biomes;
    }

    public abstract LivingEntity spawn(Location location);

    public boolean canSpawnInBiome(Biome biome) {
        for (Biome b : spawnBiomes) {
            if (b == biome) return true;
        }
        return false;
    }

    protected void setAttributeIfPresent(LivingEntity entity, Attribute attribute, double value) {
        AttributeInstance attr = entity.getAttribute(attribute);
        if (attr != null) {
            attr.setBaseValue(value);
        }
    }

    public DreamMob getMob() {
        return this;
    }
}
