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

import java.util.Calendar;
import java.util.Locale;

import se.kth.martsten.lab_2_v2.R;
import se.kth.martsten.lab_2_v2.model.Forecast;

/**
 * Class for adapting a recycler view to hold HOURS of a forecast.
 */
public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    private final Forecast.Day localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTime;
        private final TextView textViewTemperature;
        private final ImageView imageViewSymbol;
        private final TextView textViewPrecipitation;
        private final TextView textViewWindSpeed;

        public ViewHolder(View view) {
            super(view);

            textViewTime = view.findViewById(R.id.textview_hour_time);
            textViewTemperature = view.findViewById(R.id.textview_hour_temperature);
            imageViewSymbol = view.findViewById(R.id.imageview_hour_symbol);
            textViewPrecipitation = view.findViewById(R.id.textview_hour_precipitation);
            textViewWindSpeed = view.findViewById(R.id.textview_hour_windspeed);
        }

        public TextView getTextViewTime() {
            return textViewTime;
        }
        public TextView getTextViewTemperature() { return textViewTemperature; }
        public ImageView getImageViewSymbol() { return imageViewSymbol; }
        public TextView getTextViewPrecipitation() { return textViewPrecipitation; }
        public TextView getTextViewWindSpeed() { return textViewWindSpeed; }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    DayAdapter(Forecast.Day dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_hour, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Context context = viewHolder.itemView.getContext();
        Forecast.Day.Hour dataAtPosition = localDataSet.getHours().get(position);

        // set hour of day
        viewHolder.getTextViewTime().setText(context.getString(R.string.time_value, dataAtPosition.getValidTime().get(Calendar.HOUR_OF_DAY)));

        // set temperature
        double temperature = dataAtPosition.getTemperature();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String degree = sp.getString("degree", "C");
        if(degree.equals("F"))
            temperature = (temperature * 9 / 5) + 32;
        viewHolder.getTextViewTemperature().setText(context.getString(R.string.temperature_value, temperature));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if(temperature >= 0 && degree.equals("C") || temperature >= 32 && degree.equals("F"))
                viewHolder.getTextViewTemperature().setTextColor(context.getColor(R.color.red_700));
            else
                viewHolder.getTextViewTemperature().setTextColor(context.getColor(R.color.blue_700));

        // set weather symbol
        String dayNight = "day";
        Calendar validTime = dataAtPosition.getValidTime();
        if(validTime.get(Calendar.HOUR_OF_DAY) < 7 || validTime.get(Calendar.HOUR_OF_DAY) > 20)
            dayNight = "night";
        int id = context.getResources().getIdentifier(String.format(Locale.getDefault(), "%s_%d", dayNight, dataAtPosition.getSymbol()), "drawable", context.getPackageName());
        viewHolder.getImageViewSymbol().setImageDrawable(AppCompatResources.getDrawable(context, id));

        // set precipitation
        String precipString = "";
        double precip = dataAtPosition.getPrecipitation();
        if(precip > 0) precipString = context.getString(R.string.precipitation_value, dataAtPosition.getPrecipitation());
        viewHolder.getTextViewPrecipitation().setText(precipString);

        // set wind speed
        viewHolder.getTextViewWindSpeed().setText(context.getString(R.string.windspeed_value, dataAtPosition.getWindSpeed()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.getHours().size();
    }
}
