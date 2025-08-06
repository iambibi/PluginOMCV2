package fr.openmc.core.features.city.sub.notation;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.CommandsManager;
import fr.openmc.core.features.city.sub.notation.commands.AdminNotationCommands;
import fr.openmc.core.features.city.sub.notation.commands.NotationCommands;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class NotationManager {

    public static final HashMap<String, List<CityNotation>> notationPerWeek = new HashMap<>(); // weekStr -> List of CityNotation
    public static final HashMap<String, List<CityNotation>> cityNotations = new HashMap<>(); // cityUUID -> List of CityNotation
    private static Dao<CityNotation, String> notationDao;

    public NotationManager() {
        loadNotations();
        CommandsManager.getHandler().register(
                new NotationCommands(),
                new AdminNotationCommands()
        );
    }

    public static void init_db(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, CityNotation.class);
        notationDao = DaoManager.createDao(connectionSource, CityNotation.class);

    }

    public static void loadNotations() {
        try {
            List<CityNotation> notations = notationDao.queryForAll();

            notations.forEach(notation -> {
                String cityUUID = notation.getCityUUID();

                String weekStr = notation.getWeekStr();

                cityNotations.computeIfAbsent(cityUUID, k -> new java.util.ArrayList<>()).add(notation);

                notationPerWeek.computeIfAbsent(weekStr, k -> new java.util.ArrayList<>()).add(notation);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveNotations() {
        notationPerWeek.forEach((weekStr, notations) -> notations.forEach(notation -> {
                    try {
                        notationDao.createOrUpdate(notation);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    public static void createOrUpdateNotation(CityNotation notation) {
        try {
            notationDao.createOrUpdate(notation);
            String weekStr = notation.getWeekStr();
            notationPerWeek.computeIfAbsent(weekStr, k -> new java.util.ArrayList<>()).add(notation);
            cityNotations.computeIfAbsent(weekStr, k -> new java.util.ArrayList<>()).add(notation);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<CityNotation> getSortedNotationForWeek(String weekStr) {
        List<CityNotation> notations = notationPerWeek.getOrDefault(weekStr, Collections.emptyList());

        return notations.stream()
                .sorted(Comparator.comparingDouble(
                        n -> ((CityNotation) n).getNoteArchitectural() + ((CityNotation) n).getNoteCoherence()
                ).reversed())
                .collect(Collectors.toList());
    }
}
