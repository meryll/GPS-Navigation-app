package tech.hypermiles.hypermiles.Rest;

import android.content.Context;
import android.location.Location;
import android.os.Handler;

import com.birbit.android.jobqueue.JobManager;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;

import tech.hypermiles.hypermiles.Analysis.NavigationSingleton;
import tech.hypermiles.hypermiles.Other.TempAppData;
import tech.hypermiles.hypermiles.Rest.Clients.RestClient;
import tech.hypermiles.hypermiles.Rest.JobQueue.PostRoutePointJob;
import tech.hypermiles.hypermiles.Rest.Model.Route;
import tech.hypermiles.hypermiles.Rest.Model.RouteChange;
import tech.hypermiles.hypermiles.Rest.Model.RoutePoint;
import tech.hypermiles.hypermiles.Rest.Utils.RestBuilder;

/**
 * Created by Asia on 2017-05-31.
 */

public class RestManager {

    private NavigationSingleton navigationSingleton;
    private RestClient mRestClient;
    private RestBuilder mRestBuilder;
    private JobManager mJobManager;

    public RestManager(Handler mHandler, Context context)
    {
        mRestClient = new RestClient(mHandler);
        mRestBuilder = new RestBuilder();
        navigationSingleton = NavigationSingleton.getInstance();

        initJobManager(context);
    }

    private void initJobManager(Context context)
    {
        com.birbit.android.jobqueue.config.Configuration config = new com.birbit.android.jobqueue.config.Configuration.Builder( context)
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(30)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120)//wait 2 minutes)
                .build();

        mJobManager = new JobManager(config);
    }

    public void addRoutePointToJobQueue(Double currentSpeed, GeoPoint location, Boolean result)
    {
        RoutePoint routePoint = mRestBuilder.buildRoutePoint(currentSpeed, location, result);
        PostRoutePointJob job = new PostRoutePointJob(routePoint);
        mJobManager.addJobInBackground(job);
    }

    public void sentNewRoute()
    {
        try {
            mRestClient.postRoute(new Route(TempAppData.DRIVER_TO_CAR_ID));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sentRouteChange(GeoPoint location) {

        //todo z tym trzeba zrobic porzadek
        if(location==null) {
            location = new GeoPoint(50.663188, 19.489958);
        }

        RouteChange routeChange = mRestBuilder.buildRouteChange(location);

        try {
            mRestClient.postRouteChange(routeChange);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
