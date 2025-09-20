package fr.openmc.core.features.dream.items;

import lombok.Getter;

@Getter
public enum DreamRarity {
    COMMON("§b§lITEM COMMUN"),
    RARE("§b§lITEM RARE"),
    EPIC("§b§lITEM EPIQUE"),
    LEGENDARY("§b§lITEM LEGENDAIRE"),
    ONIRISIME("§b§lITEM ONIRISME");

    private final String templateLore;

    DreamRarity(String templateLore) {
        this.templateLore = templateLore;
    }
}
