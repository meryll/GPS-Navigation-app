package tech.hypermiles.hypermiles.Rest.JobQueue;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import tech.hypermiles.hypermiles.Middleware.Logger;
import tech.hypermiles.hypermiles.Rest.Clients.RestClient;
import tech.hypermiles.hypermiles.Rest.Model.RoutePoint;

/**
 * Created by Asia on 2017-03-31.
 */

public class PostRoutePointJob extends Job {
    private static final int PRIORITY = 1;
    private String text;

    private RoutePoint routePoint;
//    private RestClient mRestClient;

    public PostRoutePointJob(RoutePoint routePoint)
    {
        // This job requires network connectivity,
        // and should be persisted in case the application exits before job is completed.
        super(new Params(PRIORITY).requireNetwork().persist());
        text = routePoint.toString();
        this.routePoint = routePoint;
    }

    @Override
    public void onAdded() {
        // Job has been saved to disk.
        // This is a good place to dispatch a UI event to indicate the job will eventually run.
        // In this example, it would be good to update the UI with the newly posted tweet.
    }

    @Override
    public void onRun() throws Throwable {
        // Job logic goes here. In this example, the network call to post to Twitter is done here.
        // All work done here should be synchronous, a job is removed from the queue once
        // onRun() finishes.
        new RestClient(null).postRoutePoint(routePoint);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        // Job has exceeded retry attempts or shouldReRunOnThrowable() has decided to cancel.
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        // An error occurred in onRun.
        // Return value determines whether this job should retry or cancel. You can further
        // specify a backoff strategy or change the job's priority. You can also apply the
        // delay to the whole group to preserve jobs' running order.
        return RetryConstraint.createExponentialBackoff(runCount, 1000);
    }
}
