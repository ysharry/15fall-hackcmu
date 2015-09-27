package org.hackcmu.helloworld;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_OAUTH = 1;

    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    private GoogleApiClient mClient = null;

    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

    private int totalSteps = 0;
    private long LastSync;

    private FloatingActionButton mapButton;
    private FloatingActionButton menuButton;

    private CardView card;
    private float cardOldX;
    private float cardOldY;

    public static CityPlans mCityPlans;

    // for number animation
    private static final int FRAME_TIME_MS = 1;
    private static final String KEY = "i";
    private TextView stepCountView;
    boolean isRunning = false;

    // background updating
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                int i= msg.getData().getInt(KEY);
                stepCountView.setText(String.valueOf(i));

            } catch (Exception err) {
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());

        initialize();

        RelativeLayout main = (RelativeLayout) findViewById(R.id.main_frame);
        main.setPadding(0, getStatusBarHeight(), 0, 0);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCard();
            }
        });

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int defaultTotalSteps = 0;
        addToStepCount(sharedPref.getInt(getString(R.string.saved_total_steps), defaultTotalSteps));
        long defaultLastSync = 0;
        LastSync = sharedPref.getLong(getString(R.string.saved_last_sync), defaultLastSync);

        stepCountView = (TextView) findViewById(R.id.stepnumber);

        mapButton = (FloatingActionButton)findViewById(R.id.FAB_map);
        mapButton.setOnClickListener(new MapListener());

        FloatingActionButton fab_map = (FloatingActionButton) findViewById(R.id.FAB_map);
        fab_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(getResources().getString(R.string.step_count_intent_label), totalSteps);
                intent.setClass(MainActivity.this, MapActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        card = (CardView) findViewById(R.id.main_card);

        menuButton = (FloatingActionButton) findViewById(R.id.FAB_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCard();
            }
        });

        buildFitnessClient();

        FrameLayout backdoor = (FrameLayout) findViewById(R.id.backdoor);
        backdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getTotalSteps() > 15000) {
                    totalSteps = 3000;
                    setupCurrentCity();
                }
                addToStepCount(3000);
            }
        });
    }

    private void hideCard() {
        if(card.getVisibility() == View.VISIBLE) {
            // get the center for the clipping circle
            int cx = 20;
            int cy = card.getHeight() - 20;

            // get the initial radius for the clipping circle
            int initialRadius = card.getWidth();

            // create the animation (the final radius is zero)
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(card, cx, cy, initialRadius, 0);
            anim.setDuration(180);

            float x1 = menuButton.getX();
            float y1 = menuButton.getY();

            //ending x co-ordinates
            float x3 = cardOldX;
            float y3 = cardOldY;

            final Path path = new Path();
            path.moveTo(x1, y1);

            final float x2 = (x1 + x3) / 2;
            final float y2 = y3;

            path.quadTo(x2, y2, x3, y3);

            final ObjectAnimator fabAnimator = ObjectAnimator.ofFloat(menuButton, View.X, View.Y, path);
            fabAnimator.setInterpolator(new AccelerateInterpolator());
            fabAnimator.setDuration(130);
            fabAnimator.setStartDelay(180);

            AnimatorSet as = new AnimatorSet();
            as.playTogether(fabAnimator, anim);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    card.setVisibility(View.INVISIBLE);
                }
            });

            fabAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    menuButton.setVisibility(View.VISIBLE);
                }
            });

            // make the view visible and start the animation
            as.start();
        }
    }

    private void showCard() {
        if(card.getVisibility() == View.INVISIBLE) {
            // get the center for the clipping circle
            int cx = 20;
            int cy = card.getHeight() - 20;

            // get the final radius for the clipping circle
            int finalRadius = Math.max(card.getWidth(), card.getHeight());

            // create the animator for this view (the start radius is zero)
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(card, cx, cy, 0, finalRadius);
            anim.setDuration(180);
            anim.setStartDelay(130);

            float x1 = menuButton.getX();
            float y1 = menuButton.getY();
            cardOldX = x1;
            cardOldY = y1;

            //ending x co-ordinates
            float x3 = card.getX() + 30;
            float y3 = card.getY() + card.getHeight() - menuButton.getHeight() - 35;

            final Path path = new Path();
            path.moveTo(x1, y1);

            final float x2 = (x1 + x3) / 2;
            final float y2 = y1;

            path.quadTo(x2, y2, x3, y3);

            final ObjectAnimator fabAnimator = ObjectAnimator.ofFloat(menuButton, View.X, View.Y, path);
            fabAnimator.setInterpolator(new AccelerateInterpolator());
            fabAnimator.setDuration(200);

            AnimatorSet as = new AnimatorSet();
            as.playTogether(fabAnimator, anim);

            fabAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    menuButton.setVisibility(View.INVISIBLE);
                }
            });

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    card.setVisibility(View.VISIBLE);
                }
            });

            // make the view visible and start the animation
            as.start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(LOG_TAG, "Connecting...");
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
        isRunning = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or having
     *  multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(LOG_TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.
                                // Put application specific code here.

                                syncStepCount();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(LOG_TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(LOG_TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(LOG_TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            MainActivity.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(LOG_TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(MainActivity.this,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(LOG_TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
    }

    private void syncStepCount() {
        long endTime = getCurrentTime();
        long startTime = LastSync;
        Log.d(LOG_TAG, "start time should be: " + startTime);
        LastSync = endTime;
        Log.d(LOG_TAG, "New LastSync: " + LastSync);

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Log.i(LOG_TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(LOG_TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                // The data request can specify multiple data types to return, effectively
                // combining multiple data queries into one call.
                // In this example, it's very unlikely that the request is for several hundred
                // datapoints each consisting of a few steps and a timestamp.  The more likely
                // scenario is wanting to see how many steps were walked per day, for 7 days.
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        PendingResult<DataReadResult> pendingResult =
                Fitness.HistoryApi.readData(mClient, readRequest);

        pendingResult.setResultCallback(
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(DataReadResult dataReadResult) {
                        Log.d(LOG_TAG, "got result!!!, size: " + dataReadResult.getBuckets().size());
                        if (dataReadResult.getBuckets().size() > 0) {
                            for (Bucket bucket : dataReadResult.getBuckets()) {
                                List<DataSet> dataSets = bucket.getDataSets();
                                for (DataSet dataSet : dataSets) {
                                    // Show the data points
                                    processDataSet(dataSet);
                                }
                            }
                        }
                        Log.d(LOG_TAG, "datasets size: " + dataReadResult.getDataSets().size());
                    }
                }
        );
    }

    private void processDataSet(DataSet dataSet) {
        Log.i(LOG_TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        int newSteps = 0;

        for (DataPoint dp : dataSet.getDataPoints()) {
//            Log.i(LOG_TAG, "Data point:");
//            Log.i(LOG_TAG, "\tType: " + dp.getDataType().getName());
//            Log.i(LOG_TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
//            Log.i(LOG_TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
//                Log.i(LOG_TAG, "\tField: " + field.getName() +
//                        " Value: " + dp.getValue(field));
                if(field.getName().equals("steps")) {
                    newSteps += dp.getValue(field).asInt();
                }
            }
        }

        addToStepCount(newSteps);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_total_steps), getTotalSteps());
        editor.putLong(getString(R.string.saved_last_sync), LastSync);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private long getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        return cal.getTimeInMillis();
    }

    private long getTodayStartTime() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);

        return today.getTimeInMillis();
    }

    private void addToStepCount(int newSteps) {
        final int oldSteps = totalSteps;
        totalSteps += newSteps;
        Thread background = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = oldSteps; i <= totalSteps && isRunning; i += 3) {
                        Thread.sleep(FRAME_TIME_MS);
                        Bundle data= new Bundle();
                        data.putInt(KEY, i);
                        Message message = handler.obtainMessage();
                        message.setData(data);
                        handler.sendMessage(message);
                    }
                }
                catch (Throwable t) {
                }
            }
        });
        isRunning = true;
        background.start();
        setupCurrentCity();
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    private void initialize() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_total_steps), getTotalSteps());
        editor.putLong(getString(R.string.saved_last_sync), getTodayStartTime());
        editor.apply();
    }

    class MapListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MapActivity.class);
            MainActivity.this.startActivity(intent);

        }

    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setupCurrentCity() {
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "karla_bold.ttf");
        mCityPlans = new CityPlans(getTotalSteps());
//        mCityPlans = new CityPlans(12000);
        TextView cityname_text = (TextView) findViewById(R.id.main_cityname);
        cityname_text.setText(getResources().getString(mCityPlans.getCurrentCityName()));
        cityname_text.setTextColor(getResources().getColor(mCityPlans.getCurrentSecColor()));
        cityname_text.setTypeface(myTypeface);

        RelativeLayout main = (RelativeLayout) findViewById(R.id.main_frame);
        main.setBackground(getResources().getDrawable(mCityPlans.getCurrentBgImage()));

        TextView step_count = (TextView) findViewById(R.id.stepnumber);
        step_count.setTextColor(getResources().getColor(mCityPlans.getCurrentPrimColor()));
        step_count.setTypeface(myTypeface);

        TextView step_count_label = (TextView) findViewById(R.id.stepnumber_label);
        step_count_label.setTextColor(getResources().getColor(mCityPlans.getCurrentPrimColor()));
        step_count_label.setTypeface(myTypeface);
    }
}

