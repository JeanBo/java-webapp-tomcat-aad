package nl.microsoft.bizmilesapp.azuredemo;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by chvugrin on 11-5-2016.
 */
public class CalculateDistance implements Callable<Float> {

    Location startLocation;
    Location stopLocation;

    protected static final String TAG = "calcdistance-thread";


    CalculateDistance(Location startLocationParam, Location stopLocationParam){
        startLocation = startLocationParam;
        stopLocation = stopLocationParam;
    }

    public Float call() {
        float result = 0;

        URL url = null;
        try {
            url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + startLocation.getLatitude() + "," + startLocation.getLongitude() + "&destination=" + stopLocation.getLatitude() + "," + stopLocation.getLongitude() + "&sensor=false&units=metric&mode=driving");
            //url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=52.5116649,47.8166666&destination=52.2116983,47.8166999&sensor=false&units=metric&mode=driving");

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            JSONObject jsonObject = new JSONObject(response);
            JSONArray array = jsonObject.getJSONArray("routes");
            if(array==null || array.getJSONObject(0)==null){
                result = 0;
            }else{
                JSONObject routes = array.getJSONObject(0);
                JSONArray legs = routes.getJSONArray("legs");
                JSONObject steps = legs.getJSONObject(0);
                JSONObject distance = steps.getJSONObject("distance");
                String parsedDistance = distance.getString("value");
                result = (new Float(parsedDistance).floatValue()/1000);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException for url: " + url + " occured during calculating distance : " + e.getCause());
        } catch (ProtocolException pe) {
            Log.e(TAG, "ProtocolException occured during calculating distance : " + pe.getCause());
        } catch (IOException ioe) {
            Log.e(TAG, "IOException occured during calculating distance : " + ioe.getCause());
        } catch (JSONException e) {
            Log.e(TAG, "JSONException occured during calculating distance : " + e.getCause());
        } catch (Exception ex) {
            Log.e(TAG, "General Exception occured during calculating distance : " + ex.getMessage());
        }
        return new Float(result);
    }
}
