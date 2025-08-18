package fr.openmc.core.features.city.sub.variable.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@DatabaseTable(tableName = "city_var")
public class CityVar {

    @DatabaseField(canBeNull = false, id = true)
    private String cityUUID;

    @DatabaseField(canBeNull = false)
    @Setter
    private String scope;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    @Setter
    private Object value;

    CityVar() {
    }

    public CityVar(String cityUUID) {
        this.cityUUID = cityUUID;
    }

    public CityVar(String cityUUID, String scope, Object value) {
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