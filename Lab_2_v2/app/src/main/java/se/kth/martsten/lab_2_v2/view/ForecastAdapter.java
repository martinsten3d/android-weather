package se.kth.martsten.lab_2_v2.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Locale;

import se.kth.martsten.lab_2_v2.R;
import se.kth.martsten.lab_2_v2.model.Forecast;

/**
 * Class for adapting a recycler view to hold DAYS of a forecast.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private final Forecast localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView textViewTemperatureTitle;
        private final RecyclerView recyclerView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textview_day);
            textViewTemperatureTitle = view.findViewById(R.id.textview_day_temperature);
            recyclerView = view.findViewById(R.id.recyclerview_day);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        }

        public TextView getTextView() {
            return textView;
        }
        public RecyclerView getRecyclerView() { return recyclerView; }
        public TextView getTextViewTemperatureTitle() { return textViewTemperatureTitle; }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public ForecastAdapter(Forecast dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_day, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Context context = viewHolder.itemView.getContext();
        Forecast.Day dataAtPosition = localDataSet.getDays().get(position);

        // set date, month and weekday
        String dayLongName = dataAtPosition.getValidTime().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        int date = dataAtPosition.getValidTime().get(Calendar.DATE);
        String month = dataAtPosition.getValidTime().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String full = String.format(Locale.getDefault(), "%s %d. %s", dayLongName, date, month);
        viewHolder.getTextView().setText(full);

        // set temperature
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String degree = sp.getString("degree", "C");
        viewHolder.getTextViewTemperatureTitle().setText(context.getString(R.string.temperature, degree));

        // adapt the child recycler view to display hourly information
        viewHolder.getRecyclerView().setAdapter(new DayAdapter(dataAtPosition));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(localDataSet == null) return 0;
        return localDataSet.getDays().size();
    }
}
