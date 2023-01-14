package se.kth.martsten.lab_2_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import se.kth.martsten.lab_2_v2.io.FileIO;
import se.kth.martsten.lab_2_v2.io.INetworkResponse;
import se.kth.martsten.lab_2_v2.io.Network;
import se.kth.martsten.lab_2_v2.io.Parser;
import se.kth.martsten.lab_2_v2.model.Forecast;
import se.kth.martsten.lab_2_v2.model.Location;
import se.kth.martsten.lab_2_v2.model.Weather;
import se.kth.martsten.lab_2_v2.view.FavouritesAdapter;
import se.kth.martsten.lab_2_v2.view.ForecastAdapter;
import se.kth.martsten.lab_2_v2.view.OnClickFavouriteListener;

public class MainActivity extends AppCompatActivity implements OnClickFavouriteListener {

    private Context context;
    private Weather weather;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView recyclerViewFavourites;
    private MenuItem menuItemFavourite;

    private EditText editTextLocation;

    private RecyclerView recyclerViewForecast;

    private CountDownTimer countDownTimer;

    private final static String urlLocation = "https://www.smhi.se/wpt-a/backend_solr/autocomplete/search/%s";
    private final static String urlForecast = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/%.3f/lat/%.3f/data.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // change to a custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showContextMenu();
        getSupportActionBar().setTitle(getString(R.string.app_name));

        // drawer layout and action bar toggle
        drawerLayout = findViewById(R.id.drawerlayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setup favourites
        recyclerViewFavourites = findViewById(R.id.recyclerview_favourites);
        recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(this));

        // setup location input
        editTextLocation = findViewById(R.id.edittext_location);
        editTextLocation.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_DONE)
                getForecast(context, editTextLocation.getText().toString());
            return false;
        });

        // setup forecast view
        recyclerViewForecast = findViewById(R.id.recyclerview_forecast);
        recyclerViewForecast.setLayoutManager(new LinearLayoutManager(this));

        // load previous forecast
        weather = FileIO.loadFile(this);

        // update UI
        updateFavourites();
        updateForecast();
    }

    // inflate the appbar menu (favourites and settings)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appbar, menu);
        menuItemFavourite = menu.findItem(R.id.appbar_favourite);
        updateAppbar();
        return super.onCreateOptionsMenu(menu);
    }

    // callback from buttons in appbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.appbar_favourite:
                // if favourites contain the current forecast, remove it
                if(weather.getFavourites().contains(weather.getCurrentForecast()))
                    weather.removeFavourite(weather.getCurrentForecast());
                else
                    weather.addFavourite(weather.getCurrentForecast());

                FileIO.saveFile(context, weather);
                updateFavourites();
                updateAppbar();
                return true;
            case R.id.appbar_settings:
                Intent switchActivityIntent = new Intent(this, SettingsActivity.class);
                startActivity(switchActivityIntent);
                return true;
        }

        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        startCurrentForecastUpdateLoop();
        updateFavourites();
        updateForecast();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(countDownTimer != null) countDownTimer.cancel();
    }

    private void startCurrentForecastUpdateLoop() {
        if(weather.getCurrentForecast() == null) return;

        // if forecast is older than 10 minutes, update now
        if(System.currentTimeMillis() > weather.getCurrentForecast().getUpdatedTime() + 600000)
            getForecast(context, weather.getCurrentForecast().getLocation().getName());

        startCurrentForecastUpdateTimer();
    }

    // should never be called directly, only via startCurrentForecastUpdateLoop()
    private void startCurrentForecastUpdateTimer() {
        if(countDownTimer != null) countDownTimer.cancel();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long forecastUpdateInterval = Long.parseLong(sp.getString("update_interval", "600000"));
        countDownTimer = new CountDownTimer(forecastUpdateInterval, 1000) {
            @Override
            public void onTick(long l) { }

            @Override
            public void onFinish() {
                getForecast(context, weather.getCurrentForecast().getLocation().getName());
                startCurrentForecastUpdateTimer();
            }
        }.start();
    }

    private void getForecast(Context context, String place) {
        // send request to get coordinates based on the name of a place
        Network.sendRequest(context, String.format(urlLocation, place), new INetworkResponse() {
            @Override
            public void onResponse(String response) {
                Location location = Parser.parseCoordinates(response);
                if(location == null) {
                    Toast.makeText(context, getString(R.string.toast_location_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                // send request to get forecast data for previously gotten coordinates.
                String url = String.format(Locale.getDefault(), urlForecast, location.getLongitude(), location.getLatitude());
                Network.sendRequest(context, url, new INetworkResponse() {
                    @Override
                    public void onResponse(String response) {
                        Forecast forecast = Parser.parseForecast(location, response);
                        if(forecast == null) return;

                        weather.setCurrentForecast(forecast);
                        weather.refreshFavourites(forecast);
                        FileIO.saveFile(context, weather);
                        Toast.makeText(context, getString(R.string.toast_forecast_updated), Toast.LENGTH_SHORT).show();
                        startCurrentForecastUpdateLoop();

                        updateFavourites();
                        updateForecast();
                        updateAppbar();
                    }

                    @Override
                    public void onErrorResponse() {
                        Toast.makeText(context, getString(R.string.toast_location_error), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onOffline() { }
                });
            }

            @Override
            public void onErrorResponse() {
                Toast.makeText(context, getString(R.string.toast_location_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOffline() {
                Toast.makeText(context, getString(R.string.toast_offline), Toast.LENGTH_SHORT).show();
                if(weather.getCurrentForecast() == null) return;
                if(weather.getCurrentForecast().getLocation().getName().toLowerCase(Locale.ROOT).equals(place.toLowerCase(Locale.ROOT)))
                    weather.getCurrentForecast().setOutOfDate(true);
                updateAppbar();
            }
        });
    }

    //------------------------------------------UI---------------------------------------------
    private void updateForecast() {
        recyclerViewForecast.setAdapter(new ForecastAdapter(weather.getCurrentForecast()));
        if(weather.getCurrentForecast() != null)
            getSupportActionBar().setTitle(weather.getCurrentForecast().getLocation().getName());
    }

    private void updateFavourites() {
        recyclerViewFavourites.setAdapter(new FavouritesAdapter(weather.getFavourites(), this));
    }

    private void updateAppbar() {
        if(weather.getCurrentForecast() == null) {
            menuItemFavourite.setVisible(false);
            getSupportActionBar().setSubtitle("");
            return;
        }
        menuItemFavourite.setVisible(true);
        if(weather.getFavourites().contains(weather.getCurrentForecast()))
            menuItemFavourite.setIcon(R.drawable.ic_baseline_star_24);
        else
            menuItemFavourite.setIcon(R.drawable.ic_baseline_star_border_24);

        if(weather.getCurrentForecast().getOutOfDate()) {
            getSupportActionBar().setSubtitle(R.string.appbar_outofdate);
            return;
        }
        Calendar calendar = weather.getCurrentForecast().getApprovedTime();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        getSupportActionBar().setSubtitle(getString(R.string.appbar_approved) + format.format(calendar.getTime()));
    }

    // callback from clicking on a favourite
    @Override
    public void onFavouriteClicked(Forecast forecast) {
        weather.setCurrentForecast(forecast);
        FileIO.saveFile(context, weather);
        startCurrentForecastUpdateLoop();
        updateForecast();
        updateAppbar();
        drawerLayout.closeDrawer(GravityCompat.START);
    }
    //------------------------------------------UI---------------------------------------------
}
