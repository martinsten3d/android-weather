package se.kth.martsten.lab_2_v2.io;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Class for sending network requests and receiving responses.
 */
public class Network {

    /**
     * Sends a network request using Volley and returns the response via a callback interface.
     * @param context the current application context.
     * @param url the network URL which the request should be sent to.
     * @param networkResponse the interface used for different callbacks.
     */
    public static void sendRequest(Context context, String url, INetworkResponse networkResponse) {
        if(!isOnline(context)) {
            networkResponse.onOffline();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        networkResponse.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        networkResponse.onErrorResponse();
                    }
                }
        );
        queue.add(stringRequest);
    }

    private static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfoMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netInfoMobile != null && netInfoMobile.isConnectedOrConnecting() || netInfoWifi != null && netInfoWifi.isConnectedOrConnecting())
            return true;
        return false;
    }
}
