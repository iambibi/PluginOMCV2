package fr.openmc.core.features.city.sub.variable;

public class CityVarManager {

//    public static HashMap<String, Set<CityVar> cityVar = new HashMap<>(); // cityUUID -> Setg
//    private static Dao<CityVar, String> varsDao;
//
//    public CityVarManager() {
//        loadCityVar();
//    }
//
//    public static void initDB(ConnectionSource connectionSource) throws SQLException {
//        TableUtils.createTableIfNotExists(connectionSource, CityVar.class);
//        varsDao = DaoManager.createDao(connectionSource, CityVar.class);
//    }
//
//    public static void loadCityVar() {
//        try {
//            List<CityVar> statistics = statisticsDao.queryForAll();
//
//            statistics.forEach(statistic -> cityStatistics.put(statistic.getCityUUID(), statistic));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void saveCityStatistics() {
//        cityStatistics.forEach(
//                (city, statistics) -> {
//                    try {
//                        statisticsDao.createOrUpdate(statistics);
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                });
//    }
//
//    public static CityVar getOrCreate(String cityUUID) {
//        return cityStatistics.computeIfAbsent(cityUUID, new HashSet<>().add(new CityStatistics(city)));
//    }
//
//    public static void setStat(String cityUUID, String scope, Object value) {
//        CityStatistics stats = getOrCreate(cityUUID);
//        stats.setScope(scope);
//        stats.setValue(value);
//
//        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
//            try {
//                statisticsDao.createOrUpdate(stats);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    public static Object getStatValue(String cityUUID, String scope) {
//        CityStatistics stats = cityStatistics.get(cityUUID);
//        if (stats != null && scope.equals(stats.getScope())) {
//            return stats.getValue();
//        }
//        return null;
//    }
//
//    public static CityStatistics getStat(String cityUUID, String scope) {
//        CityStatistics stats = cityStatistics.get(cityUUID);
//        if (stats != null && scope.equals(stats.getScope())) {
//            return stats;
//        }
//        return null;
//    }
//
//
//    public static void increment(String cityUUID, String scope, long amount) {
//        CityStatistics stats = getOrCreate(cityUUID);
//        if (!scope.equals(stats.getScope())) stats.setScope(scope);
//
//        long current = stats.asLong();
//        stats.setValue(current + amount);
//
//        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
//            try {
//                statisticsDao.createOrUpdate(stats);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//    }
}
