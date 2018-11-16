package tech.hypermiles.hypermiles;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;


/**
 * Created by Asia on 2017-01-05.
 */

public class BluetoothActivity extends FragmentActivity {

    public static final String TAG = "MainActivity";
    private Button mBackToMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mBackToMapButton = (Button) findViewById(R.id.back_to_map_button);
        mBackToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        if (savedInstanceState == null) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            BluetoothFragment fragment = new BluetoothFragment();
//            transaction.replace(R.id.fragment_bluetooth, fragment);
//            transaction.commit();
//        }
    }

}
