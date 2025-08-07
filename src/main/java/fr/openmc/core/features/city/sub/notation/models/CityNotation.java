package fr.openmc.core.features.city.sub.notation.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@DatabaseTable(tableName = "city_notation")
@Getter
@Setter
public class CityNotation {
    @DatabaseField(id = true, columnName = "uuid")
    private String cityUUID;
    @DatabaseField
    private String weekStr;
    @DatabaseField(defaultValue = "null", columnName = "economy")
    private Double noteEconomy;
    @DatabaseField(defaultValue = "null", columnName = "activity")
    private Double noteActivity;
    @DatabaseField(defaultValue = "0", columnName = "architectural")
    private double noteArchitectural;
    @DatabaseField(defaultValue = "0", columnName = "coherence")
    private double noteCoherence;
    @DatabaseField(defaultValue = "0", columnName = "money")
    private double money;
    @DatabaseField
    private String description;

    CityNotation() {
        // required for ORMLite
    }

    public CityNotation(String uuid, Double noteEconomy, Double noteActivity, double noteArchitectural, double noteCoherence, String description, String weekStr) {
        this.cityUUID = uuid;
        this.noteEconomy = noteEconomy;
        this.noteActivity = noteActivity;
        this.noteArchitectural = noteArchitectural;
        this.noteCoherence = noteCoherence;
        this.weekStr = weekStr;
        this.description = description;
    }

    public double getTotalNote() {
        double total = 0;
        if (noteEconomy != null) {
            total += noteEconomy;
        }
        if (noteActivity != null) {
            total += noteActivity;
        }
        total += noteArchitectural + noteCoherence;
        return total;
    }
}
