package fr.openmc.core.features.city.mayor;

import fr.openmc.core.features.city.City;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

@Getter
public class MayorElector {
    private final City city;
    private final String electorName;
    private final UUID electorUUID;
    @Setter private NamedTextColor electorColor;
    private final int idChoicePerk2;
    private final int idChoicePerk3;
    @Setter private int vote;

    public MayorElector(City city, String electorName, UUID electorUUID, NamedTextColor electorColor, int idChoicePerk2, int idChoicePerk3, int vote) {
        this.city = city;
        this.electorName = electorName;
        this.electorUUID = electorUUID;
        this.electorColor = electorColor;
        this.idChoicePerk2 = idChoicePerk2;
        this.idChoicePerk3 = idChoicePerk3;
        this.vote = vote;
    }
}
