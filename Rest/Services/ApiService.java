package tech.hypermiles.hypermiles.Rest.Services;

/**
 * Created by Asia on 2017-02-14.
 */

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import tech.hypermiles.hypermiles.Model.Road;
import tech.hypermiles.hypermiles.Rest.Utils.RoutePOCO;
import tech.hypermiles.hypermiles.Rest.Model.*;

public interface ApiService {

    @FormUrlEncoded
    @POST("/token")
    Call<LoginResult> postLogin(@Field("Username") String userName, @Field("password") String password, @Field("grant_type") String grantType);

    @GET("/drivers")
    Call<List<Driver>> getDrivers();

    @GET("/devices")
    Call<List<Device>> getDevices();

    @GET("/cars/{carId}")
    Call<Car> getCars(@Path("carId") String id);

    @GET("/cars/driverId/{driverId}")
    Call<List<Car>> getCarsByDriverId(@Path("driverId") String id);

    @GET("/driverToCars/driverId/{driverId}")
    Call<List<DriverToCar>> getDriverToCarsByDriverId(@Path("driverId") String id);

    @GET("/routes/all")
    Call<List<Route>> getRoutes();

    @GET("/drivers/me")
    Call<Driver> getMe();

    @GET("/routes/routePoints")
    Call<List<RoutePoint>> getRoutePoints();

//    @GET("users/{username}")
//    Call<User> getUser(@Path("username") String username);
//
//    @GET("group/{id}/users")
//    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);
//
    @POST("routes/caluclateNew")
    Call<Road> getNewRoute(@Body RoutePOCO routePOCO);

    @POST("routes")
    Call<Route> createRoute(@Body Route route);

    @POST("routes/routePoints")
    Call<RoutePoint> createRoutePoint(@Body RoutePoint routePoint);

    @POST("/routes/routeChanges")
    Call<RouteChange> createRouteChange(@Body RouteChange routeChange);
}
