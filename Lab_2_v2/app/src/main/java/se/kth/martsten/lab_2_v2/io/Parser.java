package se.kth.martsten.lab_2_v2.io;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import se.kth.martsten.lab_2_v2.model.Forecast;
import se.kth.martsten.lab_2_v2.model.Location;
import se.kth.martsten.lab_2_v2.util.CalendarUtil;

/**
 * Class for parsing different JSON object from network responses to objects.
 */
public class Parser {

    /**
     * Parses a JSON network response into a Location object.
     * @param response the JSON string received from a network response.
     * @return a Location object.
     */
    public static Location parseCoordinates(String response) {
        Location location = null;
        try {
            JSONArray root = new JSONArray(response);
            if(root.length() == 0) return null;
            JSONObject place = root.getJSONObject(0);
            location = new Location(
                    place.getString("place"), place.getDouble("lon"), place.getDouble("lat"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Parses a JSON network response into a forecast object.
     * @param location a location object which will be included in the final Forecast object.
     * @param response the JSON string received from a network response.
     * @return a Forecast object.
     */
    public static Forecast parseForecast(Location location, String response) {
        Forecast forecast = null;
        try {
            JSONObject root = new JSONObject(response);
            forecast = new Forecast(location, CalendarUtil.convertToCalendar(root.getString("approvedTime")), System.currentTimeMillis(), false);
            // for every hour
            JSONArray timeSeries = root.getJSONArray("timeSeries");
            for(int i = 0; i < timeSeries.length(); i++) {
                JSONObject parametersAtTime = timeSeries.getJSONObject(i);

                Calendar validTime = CalendarUtil.convertToCalendar(parametersAtTime.getString("validTime"));
                int symbol = 1;
                double temperature = 0, precipitation = 0, windSpeed = 0;

                JSONArray parameters = parametersAtTime.getJSONArray("parameters");
                for(int j = 0; j < parameters.length(); j++) {
                    JSONObject parameter = parameters.getJSONObject(j);

                    if(parameter.getString("name").equals("t"))
                        temperature = parameter.getJSONArray("values").getDouble(0);
                    else if(parameter.getString("name").equals("Wsymb2"))
                        symbol = parameter.getJSONArray("values").getInt(0);
                    else if(parameter.getString("name").equals("pmean"))
                        precipitation = parameter.getJSONArray("values").getDouble(0);
                    else if(parameter.getString("name").equals("ws"))
                        windSpeed = parameter.getJSONArray("values").getDouble(0);
                }
                forecast.addHourToForecast(validTime, symbol, temperature, precipitation, windSpeed);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return forecast;
    }
}
