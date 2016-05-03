

package nl.microsoft.bizmilesapp.azuredemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import nl.microsoft.bizmilesapp.azuredemo.R;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener,LocationListener  {

    protected static final String TAG = "main-activity";

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    //  Azure Stuff
    private MobileServiceClient amsClient;
    private MobileServiceTable<Ride> mRidesTable;

    private Ride ride;

    //  Screen elements
    protected Location mLastLocation;
    protected Location startLocation;
    protected Location stopLocation;

    protected boolean mStartAddressRequested;
    protected boolean mStopAddressRequested;

    protected String mAddressOutput;
    private BizMilesStartReceiver mStartResultReceiver = new BizMilesStartReceiver(new Handler());
    private BizMilesStopReceiver mStopResultReceiver = new BizMilesStopReceiver(new Handler());
    protected TextView mLocationAddressTextView;
    protected ListView mRidelist;
    ProgressBar mProgressBar;

    Button mStartButton;
    Button mStopButton;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;

    //  Google API stuff
    protected GoogleApiClient mGoogleApiClient;
    protected GoogleApiClient client;
    protected LocationRequest mLocationRequest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mLocationAddressTextView = (TextView) findViewById(R.id.location_address_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mStartButton = (Button) findViewById(R.id.start_button);
        mStopButton = (Button) findViewById(R.id.stop_button);
        mRidelist = (ListView) findViewById(R.id.ridelist);
        stopLocation = null;
        startLocation = null;

        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        mRidelist.setAdapter(adapter);

        // Set defaults, then update using values stored in the Bundle.
        mStartAddressRequested = false;
        mStopAddressRequested = false;
        mAddressOutput = "";
        updateValuesFromBundle(savedInstanceState);

        //  Google API init
        buildGoogleApiClient();
        //  This was genereated by the ide for API indexing
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        createLocationRequest();

        //  Azure API init
        createAzureMobileServiceClient();
        authenticate();
        mRidesTable = amsClient.getTable(Ride.class);

        mStopButton.setVisibility(View.INVISIBLE);
        mStopButton.setEnabled(false);

        updateUIWidgets();
    }

    private synchronized void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mStartAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
                mStopAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                mLocationAddressTextView.setText(mAddressOutput);
            }
        }
    }

    private void createAzureMobileServiceClient(){
        try {
            amsClient = new MobileServiceClient("https://chris-mobileapp1.azurewebsites.net", this);

        } catch (MalformedURLException e) {

        }

        amsClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
            @Override
            public OkHttpClient createOkHttpClient() {
                OkHttpClient client = new OkHttpClient();
                client.setReadTimeout(20, TimeUnit.SECONDS);
                client.setWriteTimeout(20, TimeUnit.SECONDS);
                return client;
            }
        });

    }


    private void authenticate() {
        ListenableFuture<MobileServiceUser> mLogin = amsClient.login(MobileServiceAuthenticationProvider.WindowsAzureActiveDirectory);

        Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
            @Override
            public void onFailure(Throwable exc) {
                Toast.makeText(MainActivity.this, "Failure at authentication of service", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(MobileServiceUser user) {
            }
        });
    }

    public void stopButtonHandler(View view){


        executeStopIntentService();
        mStartAddressRequested = true;
        mStopAddressRequested = true;

        mStartButton.setVisibility(View.VISIBLE);
        mStartButton.setEnabled(true);

        mStopButton.setVisibility(View.INVISIBLE);
        mStopButton.setEnabled(false);

        updateUIWidgets();

    }

    public void startButtonHandler(View view) {

        executeStartIntentService();
        mStartAddressRequested = true;
        mStopAddressRequested = false;

        mStartButton.setVisibility(View.INVISIBLE);
        mStartButton.setEnabled(false);

        mStopButton.setVisibility(View.VISIBLE);
        mStopButton.setEnabled(true);
        adapter.clear();
        updateUIWidgets();

    }

    private void refreshItemsFromTable() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    //  Show last 3 days
                    Calendar cal = GregorianCalendar.getInstance();
                    if(cal.DAY_OF_YEAR>3)
                        cal.set( Calendar.DAY_OF_YEAR, -3);
                    final List<Ride> rides = mRidesTable.where().field("updatedAt").gt( cal.getTime()).orderBy("updatedAt", QueryOrder.Descending).execute().get();
                    final DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            for (Ride ride : rides) {
                                arrayList.add(dateFormat.format(ride.getStarted_at())+" : "+ride.getStartAddress()+","+ride.getKilometers());
                                //arrayList.add(ride.getUpdated_at()+" : "+ride.getStartAddress()+","+ride.getKilometers());
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (final Exception e) {
                    Log.e(TAG, "Exception populating ride list from database, exception: "+e.getCause());
                }

                return null;
            }
        };
        runAsyncTask(task);
    }


    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://nl.microsoft.bizmilesapp.azuredemo/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
        client.connect();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://nl.microsoft.bizmilesapp.azuredemo/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        client.disconnect();
        System.exit(0);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

    }

    private void setLocation(ActionTypes actionType){
        if(!client.isConnected())
            client.connect();
        if(!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        //  Update
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        switch(actionType){
            case START:
                startLocation = mLastLocation;
                break;
            case STOP:
                stopLocation = mLastLocation;
                break;
            case CLEAR:
                mLastLocation = null;
                startLocation = null;
                stopLocation = null;
                mLocationAddressTextView.setText("");
                break;
        }
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    protected void executeStopIntentService() {
        setLocation(ActionTypes.STOP);
        if(!(mGoogleApiClient.isConnected())){
            Toast.makeText(MainActivity.this, "not connnected", Toast.LENGTH_SHORT).show();
            return;
        }else{
            Intent stopIntent = new Intent(this, FetchAddressIntentService.class);
            stopIntent.putExtra(Constants.RECEIVER, mStopResultReceiver);
            stopIntent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
            startService(stopIntent);
        }
    }


    protected void executeStartIntentService() {
        setLocation(ActionTypes.START);
        if(!(mGoogleApiClient.isConnected())){
            Toast.makeText(MainActivity.this, "not connnected", Toast.LENGTH_SHORT).show();
            return;
        }else{
            Intent intent = new Intent(this, FetchAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER, mStartResultReceiver);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
            startService(intent);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        mProgressBar.setVisibility(ProgressBar.GONE);

        if (mStartAddressRequested) {
            mStartButton.setEnabled(false);
            mStopButton.setEnabled(true);
        }
        if(mStopAddressRequested){
            mStartButton.setEnabled(true);
            mStopButton.setEnabled(false);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mStartAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @SuppressLint("ParcelCreator")
    class BizMilesStartReceiver extends ResultReceiver {
        public BizMilesStartReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            //Toast.makeText(MainActivity.this, "Start Address received is: "+mAddressOutput, Toast.LENGTH_SHORT).show();

            ride = new Ride();
            ride.setStartAddress(mAddressOutput);
            ride.setStarted_at(new Time(GregorianCalendar.getInstance().getTimeInMillis()));
            mLocationAddressTextView.setText(mAddressOutput);

            // Reset. Enable the Fetch Address button and stop showing the progress bar
            mStartAddressRequested = true;
            updateUIWidgets();
        }
    }


    @SuppressLint("ParcelCreator")
    class BizMilesStopReceiver extends ResultReceiver {
        public BizMilesStopReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {


            Toast.makeText(MainActivity.this, "Stopped ride...", Toast.LENGTH_SHORT).show();
            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            float distance = 0;
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.CEILING);
            if(startLocation !=null && stopLocation !=null ){
                //distance = Float.parseFloat(df.format(startLocation.distanceTo(stopLocation)));
                distance = startLocation.distanceTo(stopLocation)/1000;
                Toast.makeText(MainActivity.this, "Calculated distance: "+distance, Toast.LENGTH_SHORT).show();
                //  Inserting into database, only if distance >0;
                if(distance>0){
                    ride.setKilometers(distance);
                    ride.setStopAddress(mAddressOutput);
                    ride.setStopped_at(new Time(GregorianCalendar.getInstance().getTimeInMillis()));
                    mRidesTable.insert(ride);
                }
            }
            mStopAddressRequested = true;
            setLocation(ActionTypes.CLEAR);
            refreshItemsFromTable();
            updateUIWidgets();

        }
    }

    enum ActionTypes{
        START,STOP,CLEAR;
    }
}
