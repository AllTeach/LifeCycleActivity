package com.example.lifecycleactivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class MyLocationService extends Service {

    public static final String channelID = "channelID123456";
    public static final String channelName = "Channel Name";
    private static final String CHANNEL_DEFAULT_IMPORTANCE = "1";
    private static final int ONGOING_NOTIFICATION_ID = 1;
    private static final String CHANNEL_DESCRIPTION = "description";
    private static final String TAG = "Foreground Service";
    private Notification notification;
    private NotificationManager notificationManager;
    private double lat=0,lng=0;


    // location params
    // client for location updates
    private FusedLocationProviderClient fusedLocationClient;

    // holds all the request parameters
    private LocationRequest locationRequest;

    // callback for location updates
    private LocationCallback locationCallback;


    public MyLocationService()
    {
        
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: ");
        createNotificationChannel();
        createNotification();
        // Notification ID cannot be 0.
        startForeground(ONGOING_NOTIFICATION_ID, notification);

        createLocationRequest();
        setupLocation();
        getLocationUpdates();
        return START_NOT_STICKY;
    }



    private void setupLocation() {
        fusedLocationClient = new FusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult != null) {
                    String str = "";
                    //for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Location location = locationResult.getLastLocation();
                     lng = location.getLongitude();
                     lat = location.getLatitude();

                     createNotification();
                     notificationManager.notify(ONGOING_NOTIFICATION_ID,notification);


                }
            }
        };



    }

    private void getLocationUpdates() {
        // assuming permission has been granted in Main activity
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            return;

        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channelName;
            String description = CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);

            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void createNotification() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this.getApplicationContext(), channelID);
        nb.setContentTitle("Foreground Location Service");
        nb.setContentText("Latitude " + lat + " ,Longitude " + lng);
        nb.setSmallIcon(R.drawable.ic_stat_name);
        nb.setOnlyAlertOnce(true); // no new alert every time
        nb.setChannelId(channelID);
        nb.setContentIntent(pendingIntent);
        notification = nb.build();
        Log.d(TAG, "createNotification ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
    return null;
    }

    private void createLocationRequest()
    {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1500);
        locationRequest.setSmallestDisplacement(20.0f);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}