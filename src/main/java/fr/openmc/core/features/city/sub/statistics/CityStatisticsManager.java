package fr.openmc.core.features.city.sub.statistics;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.sub.statistics.models.CityStatistics;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class CityStatisticsManager {

    public static HashMap<String, CityStatistics> cityStatistics = new HashMap<>(); // cityUUID -> CityStatistics
    private static Dao<CityStatistics, String> statisticsDao;

    public CityStatisticsManager() {
        loadCityStatistics();
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, CityStatistics.class);
        statisticsDao = DaoManager.createDao(connectionSource, CityStatistics.class);
    }

    public static void loadCityStatistics() {
        try {
            List<CityStatistics> statistics = statisticsDao.queryForAll();

            statistics.forEach(statistic -> cityStatistics.put(statistic.getCityUUID(), statistic));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveCityStatistics() {
        cityStatistics.forEach(
                (city, statistics) -> {
                    try {
                        statisticsDao.createOrUpdate(statistics);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static CityStatistics getOrCreate(String cityUUID) {
        return cityStatistics.computeIfAbsent(cityUUID, CityStatistics::new);
    }

    public static void setStat(String cityUUID, String scope, Object value) {
        CityStatistics stats = getOrCreate(cityUUID);
        stats.setScope(scope);
        stats.setValue(value);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                statisticsDao.createOrUpdate(stats);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static Object getStatValue(String cityUUID, String scope) {
        CityStatistics stats = cityStatistics.get(cityUUID);
        if (stats != null && scope.equals(stats.getScope())) {
            return stats.getValue();
        }
        return null;
    }

    public static CityStatistics getStat(String cityUUID, String scope) {
        CityStatistics stats = cityStatistics.get(cityUUID);
        if (stats != null && scope.equals(stats.getScope())) {
            return stats;
        }
        return null;
    }


    public static void increment(String cityUUID, String scope, long amount) {
        CityStatistics stats = getOrCreate(cityUUID);
        if (!scope.equals(stats.getScope())) stats.setScope(scope);

        long current = stats.asLong();
        stats.setValue(current + amount);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                statisticsDao.createOrUpdate(stats);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
