package se.kth.martsten.lab_2_v2.io;

/**
 * Interface for network callbacks.
 */
public interface INetworkResponse {

    /**
     * Successful network response.
     * @param response the response from the network request.
     */
    void onResponse(String response);

    /**
     * Network request failed for some reason but the device could reach the internet.
     */
    void onErrorResponse();

    /**
     * Device is offline.
     */
    void onOffline();
}
