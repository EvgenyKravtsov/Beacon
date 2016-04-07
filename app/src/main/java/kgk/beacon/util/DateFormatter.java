package kgk.beacon.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    public static String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        return simpleDateFormat.format(date);
    }

    public static String formatTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ROOT);
        return simpleDateFormat.format(date);
    }

    public static String formatDateAndTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy  -  HH:mm", Locale.ROOT);
        return simpleDateFormat.format(date);
    }

    public static Date[] filterForUniqueDays(ArrayList<Date> dates) {
        ArrayList<Day> days = new ArrayList<>();

        Calendar initialCalendar = Calendar.getInstance();
        for (Date date : dates) {
            initialCalendar.setTime(date);

            int year = initialCalendar.get(Calendar.YEAR);
            int month = initialCalendar.get(Calendar.MONTH);
            int day = initialCalendar.get(Calendar.DAY_OF_MONTH);

            days.add(new Day(year, month, day));
        }

        // Filtering days list for unique elements
        ArrayList<Day> unique = new ArrayList<>();
        for (Day day : days) {
            if (unique.isEmpty()) {
                unique.add(day);
            } else {
                Day uniqueDay = unique.get(unique.size() - 1);

                int year = uniqueDay.year;
                int month = uniqueDay.month;
                int dayOfMonth = uniqueDay.day;

                if (!(year == day.year && month == day.month && dayOfMonth == day.day)) {
                    unique.add(day);
                }
            }
        }

        Date[] filteredDates = new Date[unique.size()];

        int index = 0;
        Calendar calendar = Calendar.getInstance();
        for (Day day : unique) {
            calendar.set(Calendar.YEAR, day.year);
            calendar.set(Calendar.MONTH, day.month);
            calendar.set(Calendar.DAY_OF_MONTH, day.day);
            filteredDates[index++] = calendar.getTime();
        }

        return filteredDates;
    }

    private static class Day {

        private int year;
        private int month;
        private int day;

        public Day(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }

    public static String loadLastActionDateString() {
        Date date = LastActionDateStorageForActis.getInstance().load();

        if (date.compareTo(new Date(0)) == 0) {
            return  "--";
        } else {
            return DateFormatter.formatDateAndTime(date);
        }
    }

    public static long[] generateDateInterval(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long fromDateInSeconds = calendar.getTimeInMillis() / 1000;
        long toDateInSeconds = fromDateInSeconds + 86399;

        return new long[]{fromDateInSeconds, toDateInSeconds};
    }
}
