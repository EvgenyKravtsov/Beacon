package kgk.beacon.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DateFormatter {

    private static final String TAG = DateFormatter.class.getSimpleName();

    ////

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

        Set<Day> uniqueDays = new HashSet<>();
        for (Day day : days) {
            uniqueDays.add(day);
        }

        Date[] filteredDates = new Date[uniqueDays.size()];

        int index = 0;
        Calendar calendar = Calendar.getInstance();
        for (Day day : uniqueDays) {
            calendar.set(Calendar.YEAR, day.year);
            calendar.set(Calendar.MONTH, day.month);
            calendar.set(Calendar.DAY_OF_MONTH, day.day);
            filteredDates[index++] = calendar.getTime();
        }
        Arrays.sort(filteredDates, Collections.<Date>reverseOrder());

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

        @Override
        public int hashCode() {
            final int prime = 11;
            return this.year * prime + this.month * prime + this.day * prime;
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }

            if (getClass() != object.getClass()) {
                return false;
            }

            final Day day = (Day) object;

            if (this.year != day.year) {
                return false;
            }

            if (this.month != day.month) {
                return false;
            }

            if (this.day != day.day) {
                return false;
            }

            return true;
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
