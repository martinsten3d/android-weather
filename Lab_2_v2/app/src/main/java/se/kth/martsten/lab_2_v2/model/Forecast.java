package se.kth.martsten.lab_2_v2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Models a 10-day weather forecast for a specific location.
 */
public class Forecast implements Serializable {

    private final Location location;
    private final Calendar approvedTime;
    private final long updatedTime;
    private boolean outOfDate;
    private final ArrayList<Day> days;

    /**
     * Create a new Forecast.
     * @param location the location for which the forecast is valid.
     * @param approvedTime the forecasts approved time.
     * @param updatedTime the devices system time when the forecast was updated.
     * @param outOfDate true if the forecast is out-of-date.
     */
    public Forecast(Location location, Calendar approvedTime, long updatedTime, boolean outOfDate) {
        this.location = location;
        this.approvedTime = approvedTime;
        this.updatedTime = updatedTime;
        this.outOfDate = outOfDate;
        this.days = new ArrayList<>();
    }

    /**
     * Used for building the forecast hour-by-hour.
     * @param validTime the time this forecast-hour was validated.
     * @param symbol an integer representing SMHI weather symbols.
     * @param temperature the temperature this hour.
     * @param precipitation the precipitation this hour.
     * @param windSpeed the wind speed this hour.
     */
    public void addHourToForecast(Calendar validTime, int symbol, double temperature, double precipitation, double windSpeed) {
        boolean dayAlreadyExistsInForecast = false;
        for(Day day : days) {
            if(day.getValidTime().get(Calendar.DATE) == validTime.get(Calendar.DATE)) {
                day.hours.add(new Day.Hour(validTime, symbol, temperature, precipitation, windSpeed));
                dayAlreadyExistsInForecast = true;
                break;
            }
        }
        if(!dayAlreadyExistsInForecast)
            days.add(new Day(new Day.Hour(validTime, symbol, temperature, precipitation, windSpeed)));
    }

    public ArrayList<Day> getDays() {
        return new ArrayList<>(days);
    }
    public Location getLocation() { return location; }
    public Calendar getApprovedTime() { return approvedTime; }
    public long getUpdatedTime() { return updatedTime; }
    public boolean getOutOfDate() { return outOfDate; }
    public void setOutOfDate(boolean outOfDate) { this.outOfDate = outOfDate; }

    /**
     * Models one day of a forecast.
     */
    public static class Day implements Serializable {

        private final ArrayList<Hour> hours;

        /**
         * Creates a new day with at least one starting hour.
         * @param hour the starting hour of the day.
         */
        public Day(Hour hour) {
            this.hours = new ArrayList<>();
            this.hours.add(hour);
        }

        public Calendar getValidTime() {
            return hours.get(0).validTime;
        }
        public ArrayList<Hour> getHours() {
            return new ArrayList<>(hours);
        }

        /**
         * Models one hour of a forecast.
         */
        public static class Hour implements Serializable {

            private final Calendar validTime;
            private final int symbol;
            private final double temperature;
            private final double precipitation;
            private final double windSpeed;

            /**
             * Creates a new hour for the forecast with values valid for only this hour.
             * @param validTime the hour this object represent.
             * @param symbol an integer value representing SMHI weather symbols.
             * @param temperature the temperature this hour.
             * @param precipitation the precipitation this hour.
             * @param windSpeed the wind speed this hour.
             */
            public Hour(Calendar validTime, int symbol, double temperature, double precipitation, double windSpeed) {
                this.validTime = validTime;
                this.symbol = symbol;
                this.temperature = temperature;
                this.precipitation = precipitation;
                this.windSpeed = windSpeed;
            }

            public Calendar getValidTime() {
                return validTime;
            }
            public int getSymbol() { return symbol; }
            public double getTemperature() {
                return temperature;
            }
            public double getPrecipitation() { return precipitation; }
            public double getWindSpeed() { return windSpeed; }
        }
    }
}
