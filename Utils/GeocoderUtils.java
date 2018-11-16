package tech.hypermiles.hypermiles.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Asia on 2017-05-31.
 */

public class GeocoderUtils {

    private List<String> mStringAddressList;
    private List<Address> mAddressList;
    private Context mContext;

    public GeocoderUtils(Context context)
    {
        mContext = context;
        mStringAddressList = new ArrayList<>();
    }

    public List<Address> getAddressList() {
        return mAddressList;
    }

    public GeoPoint getSelectedLocation(int index)
    {
        GeoPoint destination = null;
        if (index < mStringAddressList.size()) {
            Address selected = mAddressList.get(index);
            Double latitude = selected.getLatitude();
            Double longitude = selected.getLongitude();
            destination = new GeoPoint(latitude, longitude);
        }
        return destination;
    }

    public List<String> getAddressListFromLocation(String locationName)
    {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        mStringAddressList = new ArrayList<>();

        try {

            mAddressList = geocoder.getFromLocationName(locationName, 10);
            for(int i=0;i<mAddressList.size();i++){
                mStringAddressList.add(buildString(mAddressList.get(i)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mStringAddressList;
    }

    private String buildString(Address address)
    {
        String city = address.getLocality();
        String country = address.getCountryName();
        String addressLine = address.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        return addressLine+", "+city+", "+country;
    }
}
