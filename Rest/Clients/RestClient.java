package tech.hypermiles.hypermiles.Rest.Clients;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tech.hypermiles.hypermiles.Middleware.Logger;
import tech.hypermiles.hypermiles.Model.Road;
import tech.hypermiles.hypermiles.Other.Messages;
import tech.hypermiles.hypermiles.Other.Settings;
import tech.hypermiles.hypermiles.Other.TempAppData;
import tech.hypermiles.hypermiles.Model.APIError;
import tech.hypermiles.hypermiles.Rest.Utils.RoutePOCO;
import tech.hypermiles.hypermiles.Rest.Model.Car;
import tech.hypermiles.hypermiles.Rest.Model.Driver;
import tech.hypermiles.hypermiles.Rest.Model.DriverToCar;
import tech.hypermiles.hypermiles.Rest.Model.LoginResult;
import tech.hypermiles.hypermiles.Rest.Model.Route;
import tech.hypermiles.hypermiles.Rest.Model.RouteChange;
import tech.hypermiles.hypermiles.Rest.Model.RoutePoint;
import tech.hypermiles.hypermiles.Rest.Model.SerializableLatLng;
import tech.hypermiles.hypermiles.Rest.Services.ApiService;
import tech.hypermiles.hypermiles.Rest.Adapters.ItemTypeAdapterFactory;

/**
 * Created by Asia on 2017-02-14.
 */

public class RestClient {

    private static final String TAG = "Rest client";
    private static final String BASE_URL = "http://hypermiles.azurewebsites.net";
    private ApiService apiService;
    private Converter<ResponseBody, APIError> mErrorConverter;
    private Handler mHandler;

    public RestClient(Handler handler)
    {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(Settings.TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(Settings.TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(Settings.TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);


        builder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + TempAppData.TOKEN)
                        .build();
                return chain.proceed(newRequest);
            }
        });

        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        mErrorConverter = retrofit.responseBodyConverter(APIError.class, new Annotation[0]);
        apiService = retrofit.create(ApiService.class);
        mHandler = handler;

    }

    private void sentRoutePostedMessage()
    {

        Message mMessage = Message.obtain(mHandler, Messages.ROUTE_POSTED);
        mHandler.sendMessage(mMessage);
    }

    public void login(String username, String password, Callback<LoginResult> callback)
    {
        String url = BASE_URL;

        Call<LoginResult> call = apiService.postLogin(username, password, "password");
        call.enqueue(callback);
    }


    public void getMe(Callback<Driver> callbackMethod)
    {
        Call<Driver> call = apiService.getMe();
        call.enqueue(callbackMethod);
    }

    public void getCarsByDriverId(String driverId, final Callback<List<Car>> callbackMethod)
    {
        Call<List<Car>> call = apiService.getCarsByDriverId(driverId);
        call.enqueue(callbackMethod);
    }

    public void getCarsById(String carId, final Callback<Car> callbackMethod)
    {
        Call<Car> call = apiService.getCars(carId);
        call.enqueue(callbackMethod);
    }

    public void getDriverToCarsByDriverId(String driverId, final Callback<List<DriverToCar>> callbackMethod)
    {
        Call<List<DriverToCar>> call = apiService.getDriverToCarsByDriverId(driverId);
        call.enqueue(callbackMethod);
    }

    public void getCalculatedRoute(String driverToCar, List<SerializableLatLng> waypoints, final Callback<Road> callbackMethod)
    {
        RoutePOCO routePOCO = new RoutePOCO(driverToCar, waypoints);
        try
        {
            Call<Road> call = apiService.getNewRoute(routePOCO);
            call.enqueue(callbackMethod);
        }
        catch(Exception e)
        {
            Logger.wtf(TAG, e.getMessage());
        }
    }

    public void postRoute(Route route) throws IOException {

        if(!Settings.POST_TO_SERVER) return;

        Call<Route> call = apiService.createRoute(route);

        call.enqueue(new Callback<Route>() {
            @Override
            public void onResponse(Call<Route> call, Response<Route> response) {

                if(response.errorBody()!=null)
                {
                    parseAndLogError(response);
                    return;
                }

                Route result = response.body();
                if(result != null) {
                    TempAppData.ROUTE_ID = result.getId();
                    sentRoutePostedMessage();
                }
            }

            @Override
            public void onFailure(Call<Route> call, Throwable t) {
                Logger.e(TAG, t.getMessage());
            }
        });
    }

    public void postRoutePoint(RoutePoint routePoint) throws IOException {

        if(!Settings.POST_TO_SERVER) return;

        Call<RoutePoint> call = apiService.createRoutePoint(routePoint);

        call.enqueue(new Callback<RoutePoint>() {
            @Override
            public void onResponse(Call<RoutePoint> call, Response<RoutePoint> response) {

                if(response.errorBody()!=null)
                {
                    parseAndLogError(response);
                    return;
                }
            }

            @Override
            public void onFailure(Call<RoutePoint> call, Throwable t) {
                Logger.e(TAG, "Post route point on failure "+t.getMessage());
            }
        });
    }

    public void postRouteChange(RouteChange routeChange) throws IOException {

        if(!Settings.POST_TO_SERVER) return;

        Logger.i(TAG,"Postuje route change");
        Call<RouteChange> call = apiService.createRouteChange(routeChange);

        call.enqueue(new Callback<RouteChange>() {
            @Override
            public void onResponse(Call<RouteChange> call, Response<RouteChange> response) {

                if(response.errorBody()!=null)
                {
                    parseAndLogError(response);
                    return;
                }
            }

            @Override
            public void onFailure(Call<RouteChange> call, Throwable t) {
                Logger.e(TAG, "Post route point on failure "+t.getMessage());
            }
        });
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

        try {
            error = mErrorConverter.convert(response.errorBody());
            Logger.e(TAG, error.getMessage()+" "+error.getExceptionMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return error;
    }
    public ApiService getApiService()
    {
        return apiService;
    }

}
