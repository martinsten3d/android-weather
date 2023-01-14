package se.kth.martsten.lab_2_v2.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class representing the devices/users collection of forecasts.
 */
public class Weather implements Serializable {

    private Forecast currentForecast;
    private final ArrayList<Forecast> favourites;

    /**
     * Create a new weather object.
     */
    public Weather() {
        currentForecast = null;
        this.favourites = new ArrayList<>();
    }

    /**
     * Updates the list of favourites and overwrites old forecasts with new ones.
     * @param refreshedForecast a forecasts containing new weather data.
     */
    public void refreshFavourites(Forecast refreshedForecast) {
        Forecast duplicateForecast = null;
        for(Forecast forecast : favourites)
            if(forecast.getLocation().getName().equals(refreshedForecast.getLocation().getName()))
                duplicateForecast = forecast;

        if(duplicateForecast != null) {
            int index = favourites.indexOf(duplicateForecast);
            favourites.add(index, refreshedForecast);
            favourites.remove(duplicateForecast);
        }
    }

    public void setCurrentForecast(Forecast forecast) { this.currentForecast = forecast; }
    public Forecast getCurrentForecast() { return currentForecast; }
    public void addFavourite(Forecast forecast) { favourites.add(forecast); }
    public ArrayList<Forecast> getFavourites() { return new ArrayList<>(favourites); }
    public void removeFavourite(Forecast forecast) { favourites.remove(forecast); }
}
