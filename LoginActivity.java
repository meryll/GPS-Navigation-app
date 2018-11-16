package tech.hypermiles.hypermiles;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.hypermiles.hypermiles.Analysis.NavigationSingleton;
import tech.hypermiles.hypermiles.Middleware.Logger;
import tech.hypermiles.hypermiles.Middleware.SavedDataManager;
import tech.hypermiles.hypermiles.Model.APIError;
import tech.hypermiles.hypermiles.Other.TempAppData;
import tech.hypermiles.hypermiles.Rest.Clients.RestClient;
import tech.hypermiles.hypermiles.Rest.Model.Car;
import tech.hypermiles.hypermiles.Rest.Model.CarProfile;
import tech.hypermiles.hypermiles.Rest.Model.Driver;
import tech.hypermiles.hypermiles.Rest.Model.DriverToCar;
import tech.hypermiles.hypermiles.Rest.Model.LoginResult;

/**
 * Created by Asia on 2017-03-30.
 */

public class LoginActivity extends Activity {

    private static final String TAG = "Login activity";
    private RestClient mRestClient;
    private Handler mHandler;
    private SavedDataManager mSavedDataManager;
    //------------------------------
    private ArrayAdapter<DriverToCar> mCarsAdapter;
    private List<DriverToCar> mDriverToCars;
    private DriverToCar mSelectedDriverToCar;
    //-------- UI-------------
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private TextView mErrorTextView;
    private Button mLoginButton;
    private Button mNextButton;
    private Spinner mCarsSpinner;
    private FrameLayout mLoadingView;
    private LinearLayout mLoginView;

    //todo refactor kodu lekki

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        setUpUiElements();
        initializeMessageHanlder();
        mRestClient = new RestClient(mHandler);
        mSavedDataManager = new SavedDataManager(HypermilesApplication.getInstance().getApplicationContext());
    }

    private void setUpUiElements()
    {
        mUsernameEditText = (EditText)findViewById(R.id.login_edit_text);
        mPasswordEditText = (EditText)findViewById(R.id.password_edit_text);
        mErrorTextView = (TextView)findViewById(R.id.error_text);

        mCarsSpinner = (Spinner) findViewById(R.id.cars_spinner);
        mCarsSpinner.setEnabled(false);

        mCarsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                manageSelectedCar(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                noCarSelected();
            }

        });

        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearError();
                login();
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setEnabled(false);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextButtonClicked();
            }
        });

        mLoginView = (LinearLayout) findViewById(R.id.login_view);
        mLoadingView = (FrameLayout) findViewById(R.id.loading_view);
    }

    private void initializeMessageHanlder() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

            }
        };
    }

    private void showLoginView()
    {
        mLoadingView.setVisibility(View.GONE);
        mLoginView.setVisibility(View.VISIBLE);
    }

    private void hideLoginView()
    {
        mLoadingView.setVisibility(View.VISIBLE);
        mLoginView.setVisibility(View.GONE);
    }

    private void tryAutoLogin()
    {
        mSavedDataManager.getLoginData();

        if(!canAutoLogin())
        {
            showLoginView();
            return;
        }

        autoLogin();
    }

    private void autoLogin()
    {
        getMe();
        downloadSelectedDriverToCarById();

    }

    private Boolean canAutoLogin()
    {
        Logger.i(TAG, "Cos jest nullem "+TempAppData.TOKEN+ " "+TempAppData.CAR_ID);
        return TempAppData.TOKEN!=null && TempAppData.CAR_ID!=null;
    }

    private void saveData()
    {
        //todo przerzucic to moze do klasy aplikacji? zeby bylo ladniej
        mSavedDataManager.saveLoginData();
    }

    public void login()
    {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        mRestClient.login(username, password, loginResultCallback);
    }

    private void nextButtonClicked()
    {
        if(TempAppData.TOKEN == null) {
            showAlert("You have to login first");
        } else {
            getMe();
            downloadAndParseCarProfile();
            saveData();
            startMapActivity();
        }
    }

    Callback<LoginResult> loginResultCallback = new Callback<LoginResult>() {
        @Override
        public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {

            if(response.errorBody()!=null)
            {
                parseAndLogError(response);
                showError("Zly login lub haslo.");
                return;
            }

            LoginResult result = response.body();
            if(result != null) {
                TempAppData.TOKEN = result.getAccessToken();
                getMe();
            }
        }

        @Override
        public void onFailure(Call<LoginResult> call, Throwable t) {
            showError("Server is down, probably.");
            Logger.e(TAG, t.getMessage());
        }
    };

    Callback<Driver> driverDownloadedResponseCallback = new Callback<Driver>() {
        @Override
        public void onResponse(Call<Driver> call, Response<Driver> response) {

            if(response.errorBody()!=null)
            {
                parseAndLogError(response);
                showLoginView();
                return;
            }

            Driver result = response.body();
            if(result != null) {
                TempAppData.DRIVER_ID = result.getId();
                mRestClient.getDriverToCarsByDriverId(TempAppData.DRIVER_ID, driverToCarsDownloadsResponseCallback);
            }
        }

        @Override
        public void onFailure(Call<Driver> call, Throwable t) {
            showLoginView();
            Logger.e(TAG, t.getMessage());
        }
    };

    Callback<Car> carDownloadResponseCallback = new Callback<Car>() {
        @Override
        public void onResponse(Call<Car> call, Response<Car> response) {

            if(response.errorBody()!=null)
            {
                parseAndLogError(response);
                return;
            }

            //todo refactor
            Car result = response.body();
            TempAppData.CAR_ID = result.getId();
            manageSelectedCars(response.body());
            hideLoginView();
            startMapActivity();
        }

        @Override
        public void onFailure(Call<Car> call, Throwable t) {
            showAlert("Jakis blad przy pobieraniu car");
            showLoginView();
            Logger.e(TAG, t.getMessage());
        }
    };

    Callback<List<DriverToCar>> driverToCarsDownloadsResponseCallback = new Callback<List<DriverToCar>>() {
        @Override
        public void onResponse(Call<List<DriverToCar>> call, Response<List<DriverToCar>> response) {

            if(response.errorBody()!=null)
            {
                showLoginView();
                parseAndLogError(response);
                return;
            }

            mDriverToCars = response.body();
            manageDownloadedCars();
        }

        @Override
        public void onFailure(Call<List<DriverToCar>> call, Throwable t) {
            showAlert("Jakis blad przy pobieraniu driverToCar");
            showLoginView();
            Logger.e(TAG, t.getMessage());
        }
    };

    private void noCarSelected()
    {
        mNextButton.setEnabled(false);
        showAlert("Musisz wybrac swoj pan samochodzik.");
    }

    private void manageDownloadedCars()
    {
        if(mDriverToCars==null || mDriverToCars.size()<=0) {
            showAlert("Nie masz przypisanych zadnych samochodow. Skontaktuj sie ze swoim menago.");
            return;
        }

        mNextButton.setVisibility(View.VISIBLE);
        mCarsSpinner.setEnabled(true);
        mCarsSpinner.setVisibility(View.VISIBLE);

        mCarsAdapter = new ArrayAdapter<DriverToCar>(this, android.R.layout.simple_spinner_item, mDriverToCars);
        mCarsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCarsSpinner.setAdapter(mCarsAdapter);

    }

    private void hideViews()
    {
        mCarsSpinner.setVisibility(View.INVISIBLE);
        mNextButton.setVisibility(View.INVISIBLE);
    }

    //todo refactor
    private void downloadSelectedDriverToCarById()
    {
        Logger.e(TAG, "Download selected driver to car "+TempAppData.CAR_ID);

        mRestClient.getCarsById(TempAppData.CAR_ID, carDownloadResponseCallback);
    }

    private void getSelectedDriverToCarById()
    {
        Logger.e(TAG, "Get selected driver to car");

        for(int i=0; i<mDriverToCars.size(); i++) {
            if(mDriverToCars.get(i).getId().equals(TempAppData.DRIVER_TO_CAR_ID)) {
                Logger.i(TAG, "ZNalezlismy");
                mSelectedDriverToCar = mDriverToCars.get(i);
                break;
            }
        }
        downloadAndParseCarProfile();
    }

    private void manageSelectedCar(int position)
    {
        mNextButton.setEnabled(true);
        mSelectedDriverToCar = mDriverToCars.get(position);
    }

    private void manageSelectedCars(Car car)
    {
        CarProfile selected = car.getCarProfile();
        NavigationSingleton.getInstance().setCarProfile(selected);
    }

    private void downloadAndParseCarProfile()
    {
        CarProfile selected = mSelectedDriverToCar.getCar().getCarProfile();
        NavigationSingleton.getInstance().setCarProfile(selected);

        TempAppData.DRIVER_TO_CAR_ID = mSelectedDriverToCar.getId();
        TempAppData.CAR_ID = mSelectedDriverToCar.getCarId();
    }

    private void startMapActivity()
    {
        Logger.e(TAG, "Start map activity");
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private void showError(String message)
    {
        mErrorTextView.setText(message);
    }

    private void clearError()
    {
        mErrorTextView.setText("");
    }

    private void getMe()
    {
        mRestClient.getMe(driverDownloadedResponseCallback);
    }

    private void showAlert(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private APIError parseAndLogError(Response response)
    {
        APIError error = null;
        try {
            String str = new String(response.errorBody().bytes());
            Logger.e(TAG, str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return error;
    }

    public void onResume()
    {
        Logger.i(TAG, "On resume");
        super.onResume();

        mCarsSpinner.setEnabled(false);
        mNextButton.setEnabled(false);
        hideViews();

        showLoginView();
        tryAutoLogin();
    }
}
