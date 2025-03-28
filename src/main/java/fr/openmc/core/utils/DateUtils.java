package fr.openmc.core.utils;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

public class DateUtils {
    /**
     * Get Current day of week
     * @return date (MONDAY, FRIDAY, SUNDAY, ...)
     */
    public static DayOfWeek getCurrentDayOfWeek() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E", Locale.FRENCH);

        LocalDate currentDate = LocalDate.now();
        String currentDayString = currentDate.format(formatter);

        //conversion ex ven. => FRIDAY
        return DayOfWeek.from(formatter.parse(currentDayString));
    }
    /**
     * Convert MCT to a readable duration
     * @param ticks Ticks in Minecraft
     * @return Date Format in Xj Xh Xm Xs
     */
    public static String convertTime(long ticks) {
        long millis = ticks * 50;

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;

        return String.format("%dj %dh %dm %ds", days, hours, minutes, seconds);
    }

    public static String convertSecondToTime(long seconds) {

        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;

        return String.format("%dj %dh %dm %ds", days, hours, minutes, seconds);
    }

        public static String formatRelativeDate(LocalDateTime dateTime) {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(dateTime, now);
            long minutes = duration.toMinutes();

            String repetMsg="Il y a ";
            if (minutes < 1) {
                return "À l'instant";
            } else if (minutes < 60) {
                return repetMsg + minutes + " minute" + (minutes > 1 ? "s" : "");
            } else if (duration.toHours() < 24) {
                long hours = duration.toHours();
                return repetMsg + hours + " heure" + (hours > 1 ? "s" : "");
            } else if (duration.toDays() <= 5) {
                long days = duration.toDays();
                return repetMsg + days + " jour" + (days > 1 ? "s" : "");
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Le' dd/MM/yyyy 'à' HH:mm");
                return dateTime.format(formatter);
            }
        }
    public static String getTimeUntilNextDay(DayOfWeek day) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime nextDay = now.with(TemporalAdjusters.next(day)).toLocalDate().atStartOfDay();

        Duration duration = Duration.between(now, nextDay);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        return String.format("%dd %dh %dm", days, hours, minutes);
    }
}
