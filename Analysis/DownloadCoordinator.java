package tech.hypermiles.hypermiles.Analysis;


import android.os.Handler;
import android.os.Message;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.hypermiles.hypermiles.Middleware.Logger;
import tech.hypermiles.hypermiles.Model.Road;
import tech.hypermiles.hypermiles.Other.Messages;
import tech.hypermiles.hypermiles.Other.TempAppData;
import tech.hypermiles.hypermiles.Rest.Clients.RestClient;
import tech.hypermiles.hypermiles.Rest.Model.SerializableLatLng;
import tech.hypermiles.hypermiles.Utils.LocationUtils;

/**
 * Created by Asia on 2017-05-30.
 */

public class DownloadCoordinator {

    private final String TAG = "DownloadCoordinator";
    private Road mDownloadedRoad;
    private Handler mHandler;

    public DownloadCoordinator(Handler handler) {
        mHandler = handler;
    }

    public Road getDownloadedRoad() {
        return mDownloadedRoad;
    }

    public void getNewDirection(ArrayList<GeoPoint> waypoints) {
        downloadDirection(waypoints, getNewRoadResultCallback);
    }

    public void getUpdatedDirection(ArrayList<GeoPoint> waypoints) {
        downloadDirection(waypoints, getUpdatedRoadResultCallback);
    }

    private void downloadDirection(ArrayList<GeoPoint> waypoints, Callback<Road> callbackMethod) {
        RestClient mRestClient = new RestClient(mHandler);

        List<SerializableLatLng> wayLatLng = new ArrayList<>();
        for (int i = 0; i < waypoints.size(); i++) {
            wayLatLng.add(LocationUtils.castSerializableMyLatLng(waypoints.get(i)));
        }

        mRestClient.getCalculatedRoute(TempAppData.DRIVER_TO_CAR_ID, wayLatLng, callbackMethod);
    }

    private void sentDirectionDownloadCompletedMessage() {
        Message mMessage = Message.obtain(mHandler, Messages.NEW_DIRECTION);
        mHandler.sendMessage(mMessage);
    }

    private void sentInvalidDirectionMessage() {
        Message mMessage = Message.obtain(mHandler, Messages.INVALID_DIRECTION);
        mHandler.sendMessage(mMessage);
    }

    private void sentDirectionUpdateCompletedMessage() {
        Logger.e(TAG, "Direction updated completed");

        Message mMessage = Message.obtain(mHandler, Messages.DIRECTION_UPDATE_COMPLETED);
        mHandler.sendMessage(mMessage);
    }

    Callback<Road> getNewRoadResultCallback = new Callback<Road>() {
        @Override
        public void onResponse(Call<Road> call, Response<Road> response) {

            if (response.errorBody() != null) {
                Logger.wtf(TAG, response.errorBody().toString());
                sentInvalidDirectionMessage();
                return;
            }

            Road result = response.body();
            if (result != null) {
                mDownloadedRoad = result;
                sentDirectionDownloadCompletedMessage();
            }
        }

        @Override
        public void onFailure(Call<Road> call, Throwable t) {
            Logger.e(TAG, t.getMessage());
            sentInvalidDirectionMessage();
        }
    };

    Callback<Road> getUpdatedRoadResultCallback = new Callback<Road>() {
        @Override
        public void onResponse(Call<Road> call, Response<Road> response) {

            if (response.errorBody() != null) {
                Logger.wtf(TAG, response.errorBody().toString());
                return;
            }

            Road result = response.body();
            if (result != null) {
                mDownloadedRoad = result;
                sentDirectionUpdateCompletedMessage();
            }
        }

        @Override
        public void onFailure(Call<Road> call, Throwable t) {
            Logger.e(TAG, t.getMessage());
            //sentInvalidDirectionMessage();
        }
    };
}
