package fr.openmc.core.features.dream.models.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@DatabaseTable(tableName = "save_player_dream")
@Getter
public class DBPlayerSave {
    @DatabaseField(id = true, columnName = "uuid")
    private UUID playerUUID;

    @Setter
    @DatabaseField(canBeNull = false, columnName = "inventory", dataType = DataType.LONG_STRING)
    private String inventory;

    @Setter
    @DatabaseField(columnName = "x")
    private String world;

    @Setter
    @DatabaseField(columnName = "x")
    private Double x;
    @Setter
    @DatabaseField(columnName = "y")
    private Double y;
    @Setter
    @DatabaseField(columnName = "z")
    private Double z;

    DBPlayerSave() {
        // Default constructor for ORMLite
    }

    /*
     * Constructeur qui initialise DBPlayerSave
     * - utile pour eviter les pertes des données lors d'un redemarrage du serveur
     * - et seulement au redem ou a sa connexion, on lui tp a sa derniere position
     */
    public DBPlayerSave(UUID playerUUID, String serializedInv, Location overworldLocation) {
        this.playerUUID = playerUUID;

    }

}
