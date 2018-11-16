package tech.hypermiles.hypermiles;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import android.os.Handler;
import tech.hypermiles.hypermiles.Analysis.*;
import tech.hypermiles.hypermiles.Bluetooth.BluetoothLowEnergyService;
import tech.hypermiles.hypermiles.Exceptions.DefaultUncaughtExceptionHandler;
import tech.hypermiles.hypermiles.Model.AnalysedStep;
import tech.hypermiles.hypermiles.Rest.RestManager;
import tech.hypermiles.hypermiles.Utils.AlertUtils;
import tech.hypermiles.hypermiles.Utils.ConnectivityReceiver;
import tech.hypermiles.hypermiles.Utils.FullScreenUtils;
import tech.hypermiles.hypermiles.Utils.GeocoderUtils;
import tech.hypermiles.hypermiles.Utils.LocationUtils;
import tech.hypermiles.hypermiles.Model.AnalysedRoad;
import tech.hypermiles.hypermiles.Model.Road;
import tech.hypermiles.hypermiles.Utils.MapViewUtils;
import tech.hypermiles.hypermiles.Utils.MarkersFactory;
import tech.hypermiles.hypermiles.Utils.SettingsUtils;
import tech.hypermiles.hypermiles.Middleware.*;
import tech.hypermiles.hypermiles.Other.*;
import tech.hypermiles.hypermiles.Utils.SpeedUtils;

public class MapActivity extends FragmentActivity implements LocationListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 99;
    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 50;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    
    private final int mBrakingPolylinesStartIndex = 2;
    //----- DRAWABLE STUFFS ----

    private Drawable mConnectedIcon;
    private Drawable mDisconnectedIcon;

    private final String TAG = "MAP";
    private GeoPoint placeFrom;
    private GeoPoint placeTo;
    private ArrayAdapter<String> mAutocompleteAdapter;
    private GeocoderUtils mGeocoderUtils;

    private NavigationSingleton navigationSingleton;
    private DownloadCoordinator downloadCoordinator;
    private RestManager restManager;
    private RoadAnalysis mRoadAnalysis;
    //--------- UI
    private Button mRecalculateButton;
    private Button mLogoutButton;
    private Button mBluetoothButton;
    private Button mMagicNumbersButton;
    private ToggleButton mStartStopToggle;
    private TextView mCurrentSpeedTextView;
    private TextView mConsoleTextView;
    private TextView mManeuverTextView;
    private TextView mBluetoothTextView;
    private AutoCompleteTextView mAutoCompleteTextView;
    private ImageView mInternetIcon;
    private FrameLayout mInProgressOverlay;
    //------- OSM  map --------
    private MapViewUtils mapUtils;
    private MarkersFactory markersUtils;
    private static int mMarkersOverlayIndex = 1;
    //----------------
    Handler mHandler;
    SoundService mSoundService;
    private SavedDataManager mSavedDataManager;
    //---------TASKS ------
    BrakingTask brakingTask;
    private Context mContext;
    //---------------
    private LocationManager locationManager;
    String locationProvider;

    private Boolean mStarted = false;
    private Boolean mRecalculatingDirectionInProgress = false;
    private Boolean mNavigationWasStarted = false;
    private Boolean mShouldRecalculateRouteWithInternetConnection = false;

    ArrayList<GeoPoint> waypoints = new ArrayList<>();
    private Double mCurrentSpeed = 0.0;

    private AnalysedRoad getCurrentRoad() {
        return navigationSingleton.getCurrentAnalysedRoad();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.i(TAG, "MAP ACTIVITY ON CRETAE");

        setContentView(R.layout.activity_map);

        Configuration.getInstance().load(this.getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()));

        //todo czy potrzebne jak robimy async?
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        requestMultiplePermissions();
        initialize();

        setUpMap();
        setUpDrawable();
        setUpUiElements();
    }

    private void initializeMessageHanlder() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == Messages.NEW_STEP) {
                    newStepStarted();
                }
                if (msg.what == Messages.NEW_DIRECTION) {
                    newDirectionStarted();
                }
                if (msg.what == Messages.DIRECTION_UPDATE_COMPLETED) {
                    directionUpdatedCompleted();
                }
                if (msg.what == Messages.INVALID_DIRECTION) {
                    invalidDirection();
                }
                if (msg.what == Messages.OFF_ROUTE) {
                    wentOffRoute();
                }
                if (msg.what == Messages.ROUTE_POSTED) {
                    restManager.sentRouteChange(getLastKnownLocation()); //todo w sumie to czemu?
                }
                if (msg.what == Messages.REACHED_DESTINATION) {
                    reachedDestination(); //todo w sumie to czemu?
                }
            }
        };
    }

    private void initialize() {
        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
        Logger.initialize();

        initializeLocationManager();
        initializeMessageHanlder();

        mContext = this;

        navigationSingleton = NavigationSingleton.getInstance();
        navigationSingleton.init(mHandler);
        mRoadAnalysis = new RoadAnalysis();
        restManager = new RestManager(mHandler, mContext);
        mGeocoderUtils = new GeocoderUtils(mContext);
        downloadCoordinator = new DownloadCoordinator(mHandler);
        mSoundService = new SoundService(this);
        mSavedDataManager = new SavedDataManager(HypermilesApplication.getInstance().getApplicationContext());

        setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Logger.e(TAG, "On network connection changed "+isConnected);

        if(isConnected) {
            mInternetIcon.setImageDrawable(mConnectedIcon);
            mRecalculateButton.setEnabled(true);
        } else {
            mInternetIcon.setImageDrawable(mDisconnectedIcon);
            mRecalculateButton.setEnabled(false);
        }

        if(mShouldRecalculateRouteWithInternetConnection && isConnected) {
            wentOffRoute();
            mShouldRecalculateRouteWithInternetConnection = false;
        }
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    private void setUpMap() {

        mapUtils = new MapViewUtils((MapView) findViewById(R.id.OSMmap), mContext);
        markersUtils = new MarkersFactory(mContext);

        if (checkHasLocationPermission()) {
            mapUtils.setUpMyLocationOverlay();
        }

        getLastKnownLocation();
        moveCameraToLastKnownLocation();
    }

    private void setUpDrawable()
    {
        mConnectedIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.connected);
        mDisconnectedIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.disconnected);
    }

    private void setUpUiElements() {

        mCurrentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
        mConsoleTextView = (TextView) findViewById(R.id.console);
        mBluetoothTextView = (TextView) findViewById(R.id.bluetoothConnected);
        mManeuverTextView = (TextView) findViewById(R.id.maneuver);

        mRecalculateButton = (Button) findViewById(R.id.recalculate_button);
        mRecalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recalculateButtonClicked();
            }
        });

        mLogoutButton = (Button) findViewById(R.id.logout_button);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutButtonClicked();
            }
        });

        mBluetoothButton = (Button) findViewById(R.id.bluetooth_button);
        mBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothButtonClicked();
            }
        });

        mMagicNumbersButton = (Button) findViewById(R.id.magic_numbers_button);
        mMagicNumbersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                magicNumbersButtonClicked();
            }
        });


        mStartStopToggle = (ToggleButton) findViewById(R.id.start_stop_toggle);
//        mStartStopToggle.setEnabled(false);
        mStartStopToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mStartStopToggle.isChecked()) {
                    startNavigation();
                } else {
                    stopNavigation();
                }
            }

        });

        mInProgressOverlay = (FrameLayout) findViewById(R.id.recalculating_in_progress);
        mInProgressOverlay.setVisibility(View.GONE);

        mInternetIcon = (ImageView) findViewById(R.id.internet_icon);
        if(ConnectivityReceiver.isConnected()) {
            mInternetIcon.setImageDrawable(mConnectedIcon);
        } else {
            mInternetIcon.setImageDrawable(mDisconnectedIcon);
        }

        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_textview);
        setupAutoCompleteTextView();
    }

    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    //region autocomplete shit

    public void setupAutoCompleteTextView() {

        mAutocompleteAdapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mGeocoderUtils.getAddressList());
        mAutocompleteAdapter.setNotifyOnChange(true);

        mAutoCompleteTextView.setAdapter(mAutocompleteAdapter);
        mAutoCompleteTextView.setThreshold(1);

        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                placeTo = mGeocoderUtils.getSelectedLocation(position);
            }
        });

        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                getAddressInfo(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void getAddressInfo(String locationName){
        mAutocompleteAdapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mGeocoderUtils.getAddressListFromLocation(locationName));
        mAutoCompleteTextView.setAdapter(mAutocompleteAdapter);
    }

    //endregion-----------

    private void getNewDirection() {

        if (!Settings.NAVIGATE) return;

        navigationSingleton.deletePreviousRoad();
        prepareMapForDirectionUpdate();
        new GetDirectionTask().execute();
    }

    private void updateDirection() {

        if (!Settings.NAVIGATE) return;

        prepareMapForDirectionUpdate();
        new UpdateDirectionTask().execute();
    }

    private void prepareMapForDirectionUpdate()
    {
        mapUtils.cleanMap();
        updateMarkers();
        if(!mNavigationWasStarted) mapUtils.setInitialCameraPosition(markersUtils.getBoundingBox());
        mapUtils.invalidate();
    }

    private void updateMarkers() {

        markersUtils.buildMarkerItems(waypoints);
        mapUtils.addOverlay(mMarkersOverlayIndex, markersUtils.getMarkersOverlay());
    }

    private void logoutButtonClicked()
    {
        TempAppData.TOKEN = null;
        TempAppData.DRIVER_TO_CAR_ID = null;

        mSavedDataManager.saveLoginData();

        mRecalculatingDirectionInProgress = false;
        mStarted = false;
        brakingTask = null;

        finish();
    }

    private void bluetoothButtonClicked() {
        Intent intent = new Intent(this, BluetoothActivity.class);
        startActivity(intent);
    }

    private void magicNumbersButtonClicked() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void recalculateButtonClicked() {

        //todo return different kind of error for mRecalculatingROuteInProgress
        if (!Settings.NAVIGATE || mRecalculatingDirectionInProgress) return;

        hideKeyboard();
        mapUtils.cleanMap();

        //todo do wspolnej metody
        mRecalculatingDirectionInProgress = true;
        mInProgressOverlay.setVisibility(View.VISIBLE);

        stopNavigation();
        //---------

        if (placeTo == null) {
            placeTo = new GeoPoint(51.663188, 19.489958);
        }

        placeFrom = getLastKnownLocation();

        waypoints = new ArrayList<>();
        waypoints.add(placeFrom);
        waypoints.add(placeTo);

        mConsoleTextView.setText(" ");
        getNewDirection();
    }

    private void startNavigation() {

        Logger.wtf(TAG, "START NAVIGATION");
        if (!Settings.NAVIGATE || !navigationSingleton.roadIsNotNull()) return;

        hideKeyboard();
        FullScreenUtils.setKeepScreenOn(getWindow());
        moveCameraToLastKnownLocation();

        navigationSingleton.setFirstClosestPoint(getLastKnownLocation());

        newStepStarted();
        mStarted = true;
    }

    private void moveCameraToLastKnownLocation()
    {
        mapUtils.moveNewCameraPosition(getLastKnownLocation(), Settings.CAMERA_ZOOM, 0);
    }

    private void stopNavigation() {

        if (!Settings.NAVIGATE) return;

        FullScreenUtils.clearKeepScreenOn(getWindow());
        mStarted = false;
        mapUtils.setInitialCameraPosition(markersUtils.getBoundingBox());
        mStartStopToggle.setChecked(false);
    }

    //region empty override methods

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
    //endregion

    //----------------- LOCATION ----------------------

    private void initializeLocationManager() {

        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        locationProvider = locationManager.getBestProvider(criteria, false);
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {

        if (checkHasLocationPermission()) {
            //czas w ms, odleglosc w metrach
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    private boolean checkHasLocationPermission() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Settings.NAVIGATE = true;
                    Settings.SAVE_DATA = true;
                    initializeLocationManager();
                    mapUtils.setUpMyLocationOverlay();
                } else {
                    Settings.NAVIGATE = false;
                    requestMultiplePermissions();
                }
                if (!(grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    Settings.SAVE_DATA = false;
                    requestMultiplePermissions();
                }
                break;
            }
        }
    }

    private void requestMultiplePermissions()
    {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this,
                permissions,
                MULTIPLE_PERMISSIONS_REQUEST_CODE);
    }

    private GeoPoint getLastKnownLocation() {

        if(checkHasLocationPermission())
        {
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            if(lastKnownLocation != null)
            {
                return LocationUtils.castToGeoPoint(lastKnownLocation);
            }

        }
        //todo tymczasowe
        return new GeoPoint(50.663188, 19.489958);
    }

    private void setMagicNumbers()
    {
        SettingsUtils magicNumbersHelper = new SettingsUtils(this);
        magicNumbersHelper.readValues();
    }

    private void autoconnectBluetooth()
    {
        if(!Settings.NAVIGATE) return;

        SavedDataManager savedDataManager =  new SavedDataManager(this);
        String savedMacAddress = savedDataManager.getMacAddressValue();

        if(!savedMacAddress.isEmpty() && savedMacAddress != null) {
            BluetoothLowEnergyService bles = new BluetoothLowEnergyService(getApplicationContext(), null, null, mBluetoothTextView);
            Boolean connected = bles.connect(savedMacAddress);

        }
    }

    private void updateSpeed(Location location) {

        double newSpeed = SpeedUtils.getCurrentSpeed(location);
        if(Double.compare(newSpeed, mCurrentSpeed) != 0) {
            mCurrentSpeed = newSpeed;
            redrawBrakingPoly();
            mCurrentSpeed = newSpeed;
            mCurrentSpeedTextView.setText(mCurrentSpeed+" km/h");
        }
    }

    public void onResume(){
        Logger.i(TAG, "On resume");
        super.onResume();
        setConnectivityListener(this);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    @Override
    public void onLocationChanged(Location location) {

        if(!Settings.NAVIGATE || mRecalculatingDirectionInProgress) return;

        if(!mStarted) return;

        this.updateSpeed(location);

        mapUtils.updateCamera(getLastKnownLocation(), 0);

        if(!mRecalculatingDirectionInProgress && (brakingTask==null || brakingTask.getStatus()!= AsyncTask.Status.RUNNING))
        {
            brakingTask = new BrakingTask();
            brakingTask.execute(location);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        autoconnectBluetooth();
        setMagicNumbers();
    }

    @Override
    public void onStop() {

        //todo disconnect bluetooth
        super.onStop();
    }

    //------ MESSAGES HANDLING --------
    private void reachedDestination()
    {
        Toast.makeText(this, "Dojechałeś do celu.", Toast.LENGTH_LONG).show();
    }

    private void wentOffRoute()
    {
        if(!ConnectivityReceiver.isConnected()) {
            mShouldRecalculateRouteWithInternetConnection = true;
            return;
        }

        Logger.i(TAG,"Went off route "+mRecalculatingDirectionInProgress);

        if(mRecalculatingDirectionInProgress) return;
        mRecalculatingDirectionInProgress = true;

        if(brakingTask!=null && brakingTask.getStatus() == AsyncTask.Status.RUNNING)
        {
            Logger.d(TAG,"Canceling braking task");
            brakingTask.cancel(true);
        }

        mNavigationWasStarted = mStarted;
        mStarted = false;

        waypoints.add(waypoints.size()-1, getLastKnownLocation());
        updateDirection();
    }

    private void newStepStarted()
    {
        Logger.d(TAG,"New step started");

        if(!Settings.NAVIGATE || navigationSingleton == null || navigationSingleton.getCurrentStep() == null) {
            Logger.i(TAG, "Powinno zaczac nowy step ale "+navigationSingleton.getCurrentStep());
            return;
        }
        mapUtils.updateCamera(getLastKnownLocation(), 0);
        redrawBrakingPoly();

        //todo
        //mManeuverTextView.setText(navigationSingleton.getNextStep().getInstructions()+" "+ navigationSingleton.getCurrentStep().getDuration());
    }

    private void newDirectionStarted()
    {
        restManager.sentNewRoute();

        AnalyseTask analyseTask = new AnalyseTask();
        analyseTask.execute(downloadCoordinator.getDownloadedRoad());
    }

    private void directionUpdatedCompleted()
    {
        restManager.sentRouteChange(getLastKnownLocation());

        AnalyseTask analyseTask = new AnalyseTask();
        analyseTask.execute(downloadCoordinator.getDownloadedRoad());
    }

    private void invalidDirection()
    {
        Logger.wtf(TAG, "Invalid direction");
        AlertUtils.show(mContext, "Something went wrong with getting the route.");
        finishDirectionDownload();
    }

    //------- EDN REGION ---------

    private void redrawBrakingPoly()
    {
        if(!Settings.NAVIGATE || navigationSingleton == null || navigationSingleton.getCurrentStep() == null) return;

        int brakingPolyIndex = getBrakingPolylineIndex(navigationSingleton.getCurrentStepIndex());
        Logger.i(TAG, "Braking poly index "+brakingPolyIndex);
        //todo wyrzuca blad
        try {
            mapUtils.removeOvelay(brakingPolyIndex);
            Polyline newPolyline = navigationSingleton.getCurrentStep().buildBrakingOverlay(mCurrentSpeed);
            Logger.e(TAG, "New polyline size "+newPolyline.getPoints().size()+" speed "+mCurrentSpeed);
            newPolyline.setColor(Color.CYAN);
            mapUtils.addOverlay(brakingPolyIndex,newPolyline);
        } catch(Exception e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    //todo gdzie indziej
    private int getBrakingPolylineIndex(int stepIndex)
    {
       try {
           return stepIndex+mBrakingPolylinesStartIndex+getCurrentRoad().getStepsSize();
       } catch(Exception e)
       {
           Logger.wtf(TAG, e.getMessage());
           return 0;
       }
    }

    //todo do jakiegoś oddzielnego managera?


    private void finishDirectionDownload()
    {
        //mStartStopToggle.setEnabled(true);
        mRecalculatingDirectionInProgress = false;
        mInProgressOverlay.setVisibility(View.GONE);
        mStarted = mNavigationWasStarted;
    }

    private class GetDirectionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            new CountDownTimer(Settings.TIMEOUT_IN_SECONDS, Settings.TIMEOUT_IN_SECONDS) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    if (mRecalculatingDirectionInProgress) {
                        Logger.wtf(TAG, "GetDirectionTask task finished without finishing");
                        invalidDirection();
                    }
                }
            }.start();
        }
        @Override
        protected Void doInBackground(Void... params) {

            downloadCoordinator.getNewDirection(waypoints);
            return null;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class UpdateDirectionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            new CountDownTimer(Settings.TIMEOUT_IN_SECONDS, Settings.TIMEOUT_IN_SECONDS) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    if (mRecalculatingDirectionInProgress) {
                        Logger.wtf(TAG, "Update direction task finished without finishing");
                        AlertUtils.show(mContext,"Something went wrong with updating the route.");
                        finishDirectionDownload();
                    }
                }
            }.start();
        }
        @Override
        protected Void doInBackground(Void... params) {
            downloadCoordinator.getUpdatedDirection(waypoints);
            return null;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }


    private class BrakingTask extends AsyncTask<Location, Integer, Boolean> {

        Location location;
        GeoPoint currentPoint;
        @Override
        protected Boolean doInBackground(Location... locations) {

            if(locations==null || mRecalculatingDirectionInProgress) return false;
            location = locations[0];
            currentPoint = LocationUtils.castToGeoPoint(location);
            return navigationSingleton.shouldBeBraking(currentPoint, mCurrentSpeed);
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            String oldText = mConsoleTextView.getText().toString();
            if(oldText.length()>100) {
                oldText = "";
            }

            if(result) {
                mConsoleTextView.setText(oldText + " " + result);
                BluetoothLowEnergyService.sendMessage();
                mSoundService.play();
            }

            restManager.addRoutePointToJobQueue(mCurrentSpeed,currentPoint, result);
        }
    }
    //===================== ASYNCTASKS
    private class AnalyseTask extends AsyncTask<Road, Integer, AnalysedRoad> {

        @Override
        protected AnalysedRoad doInBackground(Road... road) {
            return mRoadAnalysis.analyzeNew(road[0]);
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(AnalysedRoad currentRoad) {

            //todo bo nie wiem czy ref bedzie ten sam, pewnie tak ale upewnic sie
            currentRoad = navigationSingleton.getCurrentAnalysedRoad();

            super.onPostExecute(currentRoad);

            if(currentRoad == null || !currentRoad.stepExists()) {
                Logger.d(TAG, "AnalyseTask, current road is null");
                mRecalculatingDirectionInProgress = false;
                mInProgressOverlay.setVisibility(View.GONE);
                return;
            }

            for(int i = 0; i<currentRoad.getStepsSize(); i++)
            {
                mapUtils.addOverlay(currentRoad.getStep(i).buildRoadOverlay());
            }

            for(int i = 0; i<currentRoad.getStepsSize(); i++)
            {
                Polyline brak = currentRoad.getStep(i).buildBrakingOverlay(Settings.VELOCITY_STEP);
                mapUtils.addOverlay(brak);
            }


            navigationSingleton.startRoad(getLastKnownLocation(), placeTo);
            finishDirectionDownload();
            newStepStarted();

            AnalyseSingleTask analyseTask = new AnalyseSingleTask();
            analyseTask.execute(downloadCoordinator.getDownloadedRoad());
        }
    }

    private class AnalyseSingleTask extends AsyncTask<Road, Integer, AnalysedStep> {

        @Override
        protected AnalysedStep doInBackground(Road... road) {
            Logger.i("TAG", "Zaczynamy analizowanie pojedynczego");
            return mRoadAnalysis.analyzeSingle();
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(AnalysedStep step) {

            super.onPostExecute(step);

            if(step==null) return;

            mapUtils.addOverlay(step.buildRoadOverlay());
            Polyline brak = step.buildBrakingOverlay(Settings.VELOCITY_STEP);
            mapUtils.addOverlay(brak);

            AnalyseSingleTask analyseTask = new AnalyseSingleTask();
            analyseTask.execute(downloadCoordinator.getDownloadedRoad());
        }
    }
}