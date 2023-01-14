package se.kth.martsten.lab_2_v2.model;

import java.io.Serializable;

/**
 * Class representing a physical location in the world.
 */
public class Location implements Serializable {

    private final String name;
    private final double longitude, latitude;

    /**
     * Creates a new location which represents a real location in the world.
     * @param name the name of the location.
     * @param longitude the longitude coordinate for this location.
     * @param latitude the latitude coordinate for this locations.
     */
    public Location(String name, double longitude, double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() { return name; }
    public double getLongitude() { return longitude; }
    public double getLatitude() { return latitude; }
}
