package tech.hypermiles.hypermiles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import tech.hypermiles.hypermiles.Exceptions.DefaultUncaughtExceptionHandler;
import tech.hypermiles.hypermiles.Utils.SettingsUtils;
import tech.hypermiles.hypermiles.Other.Settings;

public class SettingsActivity extends AppCompatActivity {

    private Button mKeyboardButton;
    private Button mBackButton;
    private Button mSaveButton;

    private EditText MIN_DISTANCE;
    private EditText MAX_DISTANCE;
    private EditText VARIANT;
    private EditText CAR_WEIGHT;
    private EditText DECELERATION_PROFILE;
    private EditText FIXED_ACCELERATION;
    private EditText TURN_SHARP;
    private EditText UTURN;
    private EditText TURN_SLIGHT;
    private EditText MERGE;
    private EditText MERGE_STRAIGHT;
    private EditText MERGE_SLIGHT;
    private EditText MERGE_SHARP;
    private EditText ROUNDABOUT;
    private EditText TURN;
    private EditText RAMP;
    private EditText RAMP_STRAIGHT;
    private EditText RAMP_SLIGHT;
    private EditText RAMP_SHARP;
    private EditText STRAIGHT;
    private EditText FORK;
    private EditText FERRY;
    private EditText KEEP;
    private ToggleButton NAVIGATE;
    private ToggleButton POST_TO_SERVER;
    private ToggleButton USE_MAX_SPEED;

    private SettingsUtils mSettingsHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());

        setContentView(R.layout.activity_settings);

        mSettingsHelper = new SettingsUtils(this);
        setUiElements();
        setValues();
    }

    private void setUiElements()
    {

        mBackButton = (Button) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButtonClicked();
            }
        });

        NAVIGATE = (ToggleButton) findViewById(R.id.SHOULD_NAVIGATE);
        POST_TO_SERVER = (ToggleButton) findViewById(R.id.POST_TO_SERVER);
        USE_MAX_SPEED = (ToggleButton) findViewById(R.id.USE_MAX_SPEED);

        MIN_DISTANCE = (EditText) findViewById(R.id.MIN_DISTANCE);
        MAX_DISTANCE = (EditText) findViewById(R.id.MAX_DISTANCE);
        CAR_WEIGHT = (EditText) findViewById(R.id.CAR_WEIGHT);
        VARIANT = (EditText) findViewById(R.id.VARIANT);
        DECELERATION_PROFILE = (EditText) findViewById(R.id.DECELERATION_PROFILE);
        FIXED_ACCELERATION = (EditText) findViewById(R.id.FIXED_ACCELERATION);
        TURN_SHARP = (EditText) findViewById(R.id.TURN_SHARP);
        UTURN = (EditText) findViewById(R.id.UTURN);
        TURN_SLIGHT = (EditText) findViewById(R.id.TURN_SLIGHT);
        MERGE = (EditText) findViewById(R.id.MERGE);
        MERGE_SLIGHT = (EditText) findViewById(R.id.MERGE_SLIGHT);
        MERGE_SHARP = (EditText) findViewById(R.id.MERGE_SHARP);
        MERGE_STRAIGHT = (EditText) findViewById(R.id.MERGE_STRAIGHT);
        ROUNDABOUT = (EditText) findViewById(R.id.ROUNDABOUT);
        TURN = (EditText) findViewById(R.id.TURN);
        RAMP = (EditText) findViewById(R.id.RAMP);
        RAMP_SLIGHT = (EditText) findViewById(R.id.RAMP_SLIGHT);
        RAMP_SHARP = (EditText) findViewById(R.id.RAMP_SHARP);
        RAMP_STRAIGHT = (EditText) findViewById(R.id.RAMP_STRAIGHT);
        STRAIGHT = (EditText) findViewById(R.id.STRAIGHT);
        FORK = (EditText) findViewById(R.id.FORK);
        FERRY = (EditText) findViewById(R.id.FERRY);
        KEEP = (EditText) findViewById(R.id.KEEP);
    }


    private void setValues()
    {
        MIN_DISTANCE.setText(""+ Settings.MIN_DISTANCE);
        MAX_DISTANCE.setText(""+ Settings.MAX_DISTANCE);
        VARIANT.setText(""+ Settings.VARIANT);
        CAR_WEIGHT.setText(""+ Settings.CAR_WEIGHT);
        DECELERATION_PROFILE.setText(""+ Settings.DECELERATION_PROFILE);
        FIXED_ACCELERATION.setText(""+ Settings.FIXED_ACCELERATION);
        TURN_SHARP.setText(""+ Settings.TURN_SHARP);
        UTURN.setText(""+ Settings.UTURN);
        TURN_SLIGHT.setText(""+ Settings.TURN_SLIGHT);
        MERGE.setText(""+ Settings.MERGE);
        MERGE_SHARP.setText(""+ Settings.MERGE_SHARP);
        MERGE_SLIGHT.setText(""+ Settings.MERGE_SLIGHT);
        MERGE_STRAIGHT.setText(""+ Settings.MERGE_STRAIGHT);
        ROUNDABOUT.setText(""+ Settings.ROUNDABOUT);
        TURN.setText(""+ Settings.TURN);
        RAMP.setText(""+ Settings.RAMP);
        RAMP_SLIGHT.setText(""+ Settings.RAMP_SLIGHT);
        RAMP_SHARP.setText(""+ Settings.RAMP_SHARP);
        RAMP_STRAIGHT.setText(""+ Settings.RAMP_STRAIGHT);
        STRAIGHT.setText(""+ Settings.STRAIGHT);
        FORK.setText(""+ Settings.FORK);
        FERRY.setText(""+ Settings.FERRY);
        KEEP.setText(""+ Settings.KEEP);
        NAVIGATE.setChecked(Settings.NAVIGATE);
        POST_TO_SERVER.setChecked(Settings.POST_TO_SERVER);
        USE_MAX_SPEED.setChecked(Settings.USE_MAX_SPEED);
    }

    private void saveButtonClicked()
    {
        Settings.MIN_DISTANCE = Integer.parseInt(String.valueOf(MIN_DISTANCE.getText()));
        Settings.MAX_DISTANCE = Integer.parseInt(String.valueOf(MAX_DISTANCE.getText()));
        Settings.VARIANT = Integer.parseInt(String.valueOf(VARIANT.getText()));
        Settings.CAR_WEIGHT = Double.parseDouble(String.valueOf(CAR_WEIGHT.getText()));
        Settings.DECELERATION_PROFILE = Double.parseDouble(String.valueOf(DECELERATION_PROFILE.getText()));
        Settings.FIXED_ACCELERATION = Double.parseDouble(String.valueOf(FIXED_ACCELERATION.getText()));
        Settings.TURN_SHARP = Double.parseDouble(String.valueOf(TURN_SHARP.getText()));
        Settings.UTURN = Double.parseDouble(String.valueOf(UTURN.getText()));
        Settings.TURN_SLIGHT = Double.parseDouble(String.valueOf(TURN_SLIGHT.getText()));
        Settings.MERGE = Double.parseDouble(String.valueOf(MERGE.getText()));
        Settings.MERGE_SLIGHT = Double.parseDouble(String.valueOf(MERGE_SLIGHT.getText()));
        Settings.MERGE_STRAIGHT = Double.parseDouble(String.valueOf(MERGE_STRAIGHT.getText()));
        Settings.MERGE_SHARP = Double.parseDouble(String.valueOf(MERGE_SHARP.getText()));
        Settings.ROUNDABOUT = Double.parseDouble(String.valueOf(ROUNDABOUT.getText()));
        Settings.TURN = Double.parseDouble(String.valueOf(TURN.getText()));
        Settings.RAMP = Double.parseDouble(String.valueOf(RAMP.getText()));
        Settings.RAMP_SLIGHT = Double.parseDouble(String.valueOf(RAMP_SLIGHT.getText()));
        Settings.RAMP_SHARP = Double.parseDouble(String.valueOf(RAMP_SHARP.getText()));
        Settings.RAMP_STRAIGHT = Double.parseDouble(String.valueOf(RAMP_STRAIGHT.getText()));

        Settings.STRAIGHT = Double.parseDouble(String.valueOf(STRAIGHT.getText()));
        Settings.FORK = Double.parseDouble(String.valueOf(FORK.getText()));
        Settings.FERRY = Double.parseDouble(String.valueOf(FERRY.getText()));
        Settings.KEEP = Double.parseDouble(String.valueOf(KEEP.getText()));
        Settings.NAVIGATE = NAVIGATE.isChecked();
        Settings.POST_TO_SERVER = POST_TO_SERVER.isChecked();
        Settings.USE_MAX_SPEED = USE_MAX_SPEED.isChecked();

        mSettingsHelper.saveValues();
    }


}
