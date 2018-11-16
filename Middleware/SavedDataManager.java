package tech.hypermiles.hypermiles.Middleware;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import tech.hypermiles.hypermiles.Other.TempAppData;

/**
 * Created by Asia on 2017-01-24.
 */

//todo pewnie mozna to jakos ujednolicic
public class SavedDataManager {

    private FragmentActivity mFragmentActivity;
    private Activity mActivity;
    private Context mContext;
    private final String MAC_ADDRESS_KEY_NAME = "MAC_ADDRESS";
    private final String BLUETOOTH_FILE_NAME = "BLUETOOTH";
    private final String MAGIC_NUMBERS_FILE_NAME = "MAGIC_NUMBERS";
    private final String LOGIN_DATA_FILE_NAME = "LOGIN_DATA";

    public SavedDataManager(FragmentActivity activity)
    {
        this.mFragmentActivity = activity;
    }

//    public SavedDataManager(Activity activity)
//    {
//        this.mActivity = activity;
//    }

    public SavedDataManager(Context context)
    {
        this.mContext = context;
    }

    public String getMacAddressValue()
    {
        if(mFragmentActivity ==null) return "";

        SharedPreferences sharedPref = mFragmentActivity.getSharedPreferences(BLUETOOTH_FILE_NAME, Context.MODE_PRIVATE);
        String saved_mac = sharedPref.getString(MAC_ADDRESS_KEY_NAME, "");
        return saved_mac;
    }

    public void setMacAddressValue(String newValue)
    {
        if(mFragmentActivity ==null) return;

        SharedPreferences sharedPref = mFragmentActivity.getSharedPreferences(BLUETOOTH_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MAC_ADDRESS_KEY_NAME, newValue);
        editor.commit();
    }

    public Double getMagicNumberValue(String key)
    {
        if(mFragmentActivity ==null) return 0.0;

        SharedPreferences sharedPref = mFragmentActivity.getSharedPreferences(MAGIC_NUMBERS_FILE_NAME, Context.MODE_PRIVATE);
        Float saved_mac = sharedPref.getFloat(key, -1.0f);
        return Double.valueOf(saved_mac);
    }

    public void setMagicNumberValue(Double newValue, String keyName)
    {
        if(mFragmentActivity ==null) return;

        SharedPreferences sharedPref = mFragmentActivity.getSharedPreferences(MAGIC_NUMBERS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(keyName, newValue.floatValue());
        editor.commit();
    }

    public void saveLoginData()
    {
        if(mContext ==null) return;

        SharedPreferences sharedPref = mContext.getSharedPreferences(LOGIN_DATA_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Token", TempAppData.TOKEN);
        editor.putString("DriverToCar", TempAppData.DRIVER_TO_CAR_ID);
        editor.putString("Car", TempAppData.CAR_ID);
        editor.commit();
    }

    public void getLoginData()
    {
        if(mContext ==null) return;

        SharedPreferences sharedPref = mContext.getSharedPreferences(LOGIN_DATA_FILE_NAME, Context.MODE_PRIVATE);
        TempAppData.TOKEN = sharedPref.getString("Token", null);
        TempAppData.DRIVER_TO_CAR_ID = sharedPref.getString("DriverToCar", null);
        TempAppData.CAR_ID = sharedPref.getString("Car", null);
    }

}
