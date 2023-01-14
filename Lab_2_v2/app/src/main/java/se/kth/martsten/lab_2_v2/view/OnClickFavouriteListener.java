package se.kth.martsten.lab_2_v2.view;

import se.kth.martsten.lab_2_v2.model.Forecast;

/**
 * Interface for registering clicks on view objects in the favourites list.
 */
public interface OnClickFavouriteListener {

    /**
     * Called when the user clicked on a view representing a favourite forecast.
     * @param forecast the affected forecast.
     */
    void onFavouriteClicked(Forecast forecast);
}
