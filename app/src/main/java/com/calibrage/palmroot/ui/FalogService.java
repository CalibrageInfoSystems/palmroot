package com.calibrage.palmroot.ui;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Palm3FoilDatabase;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.datasync.helpers.DataSyncHelper;
import com.calibrage.palmroot.dbmodels.UserDetails;

public class FalogService extends Service implements LocationListener {

    private static final String LOG_TAG = "MyService";

    private static LocationProvider mLocationProvider;
    private static String latLong = "";
//    private final int INTERVAL = 5000;
//    private Timer timer = new Timer();

    PowerManager.WakeLock wakeLock;
    public Context context;
    double latitude, longitude;
    private Palm3FoilDatabase palm3FoilDatabase;
    private static final int MIN_UPDATE_TIME = 0;
    private static final int MIN_UPDATE_DISTANCE = 250;
    private Location location;

    public LocationManager locationManager;
    public String CreatedDate, UpdatedDate, ServerUpdatedStatus, CreatedByUserId, UpdatedByUserId, IsActive, IMEINumber;
    public String USER_ID_TRACKING;
    public static final String ACTION_LOCATION_UPDATED_TRACKING = "com.oilpalm3f.mainapp.falogService.location.updated";
    public static final String ACTION_START = "com.oilpalm3f.mainapp.falogService.start";
    public static final String TRACKING_LONGITUDE = "geo_longitude";
    public static final String TRACKING_LATITUDE = "geo_latitude";
    private DataAccessHandler dataAccessHandler = null;


    @Override
    public void onCreate() {
        super.onCreate();
//        palm3FoilDatabase = new Palm3FoilDatabase(this);
        Log.v(LOG_TAG, "Congrats! MyService Created");
//        Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "onCreate");


    }

    public static LocationProvider getLocationProvider(Context context, boolean showDialog) {
        if (mLocationProvider == null) {
            mLocationProvider = new LocationProvider(context, new LatLongListener() {
                @Override
                public void getLatLong(String mLatLong) {
                    latLong = mLatLong;
                }
            });

        }
        if (mLocationProvider.getLocation(showDialog)) {
            return mLocationProvider;
        } else {
            return null;
        }

    }

    public String getLatLong(Context context, boolean showDialog) {

        mLocationProvider = getLocationProvider(context, showDialog);

        if (mLocationProvider != null) {
            latLong = mLocationProvider.getLatitudeLongitude();


        }
        return latLong;
    }


    public void startLocationService(ApplicationThread.OnComplete onComplete) {
        Log.d(LOG_TAG, "start location service");
        String providerType = null;
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            boolean gpsProviderEnabled = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkProviderEnabled = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (gpsProviderEnabled) {
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    providerType = "gps";
                    com.calibrage.palmroot.cloudhelper.Log.d(LOG_TAG, "gps lbs provider:" + (location == null ? "null" : String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude())));
                    //updateLocation(location);
                }
            }
            if (networkProviderEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    providerType = "network";
                    com.calibrage.palmroot.cloudhelper.Log.d(LOG_TAG, "network lbs provider:" + (location == null ? "null" : String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude())));
                    // updateLocation(location);
                }
            }


        } catch (Exception e) {
            Log.e(LOG_TAG, "Cannot get location", e);
        }

        if (onComplete != null) {
            onComplete.execute(location != null, location, providerType);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        IMEINumber = CommonUtils.getIMEInumber(this);
        context = getApplicationContext();
        com.calibrage.palmroot.cloudhelper.Log.d(LOG_TAG, "start location service & location listener");
        // mahesh uncomented
        ApplicationThread.nuiPost(LOG_TAG, "start lococation service", new Runnable() {
            @Override
            public void run() {
                startLocationService(null);

            }
        });
        // end
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.print_logo)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);

        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("running service")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(1, notification);
        }
//        return START_NOT_STICKY;

        try {
            palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
            palm3FoilDatabase.createDataBase();
            dataAccessHandler = new DataAccessHandler(context);
        } catch (Exception e) {
            e.getMessage();
        }

        String query = Queries.getInstance().getUserDetailsNewQuery(CommonUtils.getIMEInumber(this));

        DataAccessHandler dataAccessHandler = new DataAccessHandler(this);
        final UserDetails userDetails = (UserDetails) dataAccessHandler.getUserDetails(query, 0);

        if (null != userDetails) {
            USER_ID_TRACKING = userDetails.getId();
            Log.v(LOG_TAG, "Start Service userId" + USER_ID_TRACKING);
        }

//        return super.onStartCommand(intent, flags, startId);
        return  START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }


    @Override
    public void onLocationChanged(Location location) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appprefs", MODE_PRIVATE);
        boolean isFreshInstall = sharedPreferences.getBoolean(CommonConstants.IS_FRESH_INSTALL, true);

        if (location != null) {

            String latlong[] = getLatLong(FalogService.this, false).split("@");

//            Toast.makeText(getApplicationContext(), "location "+ String.valueOf(location.getLatitude()) + "/" + String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();

            Log.d(LOG_TAG, "updateTracking location:" + String.valueOf(location.getLatitude()) + "/" + String.valueOf(location.getLongitude()));
            latitude = Double.parseDouble(latlong[0]);
            longitude = Double.parseDouble(latlong[1]);
            CommonConstants.Current_Latitude = latitude;
            CommonConstants.Current_Longitude = longitude;


            CreatedDate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS_SSS);
            UpdatedDate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS_SSS);
            ServerUpdatedStatus = CommonConstants.ServerUpdatedStatus;
            CreatedByUserId = USER_ID_TRACKING;
            UpdatedByUserId = USER_ID_TRACKING;

            IsActive = "1";

            String selectedLatLong = dataAccessHandler.getFalogLatLongs(Queries.getInstance().queryVerifyFalogDistance());
            if (!TextUtils.isEmpty(selectedLatLong)) {
                Log.v(LOG_TAG, "@@@@ data " + selectedLatLong);
                double actualDistance = 0;
                String[] yieldDataArr = selectedLatLong.split("-");

                if (yieldDataArr.length > 0 && !TextUtils.isEmpty(yieldDataArr[0]) && !TextUtils.isEmpty(yieldDataArr[1])) {

                    actualDistance = CommonUtils.distance(latitude, longitude,
                            Double.parseDouble(yieldDataArr[0]),
                            Double.parseDouble(yieldDataArr[1]), 'm');

                }

                Log.v(LOG_TAG, "@@@@ actual distance " + actualDistance);

                if (actualDistance >= 250) {

                    palm3FoilDatabase.insertLatLong(latitude, longitude, IsActive, CreatedByUserId, CreatedDate, UpdatedByUserId, UpdatedDate, IMEINumber, ServerUpdatedStatus);

                    DataSyncHelper.sendTrackingData(context, new ApplicationThread.OnComplete() {
                        @Override
                        public void execute(boolean success, Object result, String msg) {
                            if (success) {
                                Log.v(LOG_TAG, "sent success");
                            } else {
                                Log.e(LOG_TAG, "sent failed");
                            }
                        }
                    });
                } else {

//                    UiUtils.showCustomToastMessage("plz wiat for 250M", context, 0);

                }
            } else {

                palm3FoilDatabase.insertLatLong(latitude, longitude, IsActive, CreatedByUserId, CreatedDate, UpdatedByUserId, UpdatedDate, IMEINumber, ServerUpdatedStatus);

                DataSyncHelper.sendTrackingData(context, new ApplicationThread.OnComplete() {
                    @Override
                    public void execute(boolean success, Object result, String msg) {
                        if (success) {
                            com.calibrage.palmroot.cloudhelper.Log.v(LOG_TAG, "sent success");
                        } else {
                            com.calibrage.palmroot.cloudhelper.Log.e(LOG_TAG, "sent failed");
                        }
                    }
                });
            }


        }


    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}