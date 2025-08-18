package fr.openmc.core.features.city.sub.statistics.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@DatabaseTable(tableName = "city_statistics")
public class CityStatistics {

    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private String cityUUID;

    @DatabaseField(canBeNull = false, uniqueCombo = true)
    @Setter
    private String scope;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    @Setter
    private Object value;

    CityStatistics() {
    }

    public CityStatistics(String cityUUID) {
        this.cityUUID = cityUUID;
    }

    public CityStatistics(String cityUUID, String scope, Object value) {
        this.cityUUID = cityUUID;
        this.scope = scope;
        this.value = value;
    }

    public int asInt() {
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    public long asLong() {
        return value instanceof Number ? ((Number) value).longValue() : 0L;
    }

    public double asDouble() {
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }

    public String asString() {
        return value != null ? value.toString() : null;
    }
}