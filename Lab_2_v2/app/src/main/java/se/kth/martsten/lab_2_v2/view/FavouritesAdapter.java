package se.kth.martsten.lab_2_v2.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import se.kth.martsten.lab_2_v2.R;
import se.kth.martsten.lab_2_v2.model.Forecast;

/**
 * Class for adapting a recycler view to hold FAVOURITES.
 */
public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    private final ArrayList<Forecast> localDataSet;
    private final OnClickFavouriteListener listener;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewLocation;
        private final TextView textViewTemperature;
        private final TextView textViewPrecipitation;
        private final TextView textViewWindspeed;
        private final ImageView imageViewSymbol;

        public ViewHolder(View view) {
            super(view);
            textViewLocation = view.findViewById(R.id.textview_favourite_location);
            textViewTemperature = view.findViewById(R.id.textview_favourite_temperature);
            textViewPrecipitation = view.findViewById(R.id.textview_favourite_percipitation);
            textViewWindspeed = view.findViewById(R.id.textview_favourite_windspeed);
            imageViewSymbol = view.findViewById(R.id.imageview_favourite_symbol);
        }

        public TextView getTextViewLocation() {
            return textViewLocation;
        }
        public TextView getTextViewTemperature() { return textViewTemperature; }
        public TextView getTextViewPrecipitation() { return textViewPrecipitation; }
        public TextView getTextViewWindspeed() { return textViewWindspeed; }
        public ImageView getImageViewSymbol() { return imageViewSymbol; }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public FavouritesAdapter(ArrayList<Forecast> dataSet, OnClickFavouriteListener listener) {
        localDataSet = dataSet;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_favourite, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Context context = viewHolder.itemView.getContext();
        Forecast dataAtPosition = localDataSet.get(position);
        Forecast.Day.Hour hour = dataAtPosition.getDays().get(0).getHours().get(0);

        // set location name
        viewHolder.getTextViewLocation().setText(dataAtPosition.getLocation().getName());

        // set temperature
        double temperature = hour.getTemperature();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String degree = sp.getString("degree", "C");
        if(degree.equals("F"))
            temperature = (temperature * 9 / 5) + 32;
        viewHolder.getTextViewTemperature().setText(context.getString(R.string.favourites_temperature_value, temperature, degree));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if(temperature >= 0 && degree.equals("C") || temperature >= 32 && degree.equals("F"))
                viewHolder.getTextViewTemperature().setTextColor(context.getColor(R.color.red_700));
            else
                viewHolder.getTextViewTemperature().setTextColor(context.getColor(R.color.blue_700));

        // set precipitation
        viewHolder.getTextViewPrecipitation().setText(context.getString(R.string.favourites_precipitation_value, hour.getPrecipitation()));

        // set wind speed
        viewHolder.getTextViewWindspeed().setText(context.getString(R.string.favourites_windspeed_value, hour.getWindSpeed()));

        // set symbol
        String dayNight = "day";
        Calendar validTime = hour.getValidTime();
        if(validTime.get(Calendar.HOUR_OF_DAY) < 7 || validTime.get(Calendar.HOUR_OF_DAY) > 20)
            dayNight = "night";
        int id = context.getResources().getIdentifier(String.format(Locale.getDefault(), "%s_%d", dayNight, hour.getSymbol()), "drawable", context.getPackageName());
        viewHolder.getImageViewSymbol().setImageDrawable(AppCompatResources.getDrawable(context, id));

        // register callback for clicking on favourite
        viewHolder.itemView.setOnClickListener(view -> listener.onFavouriteClicked(dataAtPosition));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
