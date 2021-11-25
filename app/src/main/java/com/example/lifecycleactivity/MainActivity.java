package com.example.lifecycleactivity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Map;
import java.util.concurrent.BlockingDeque;

public class MainActivity extends AppCompatActivity {

    //private  MediaPlayer mediaPlayer;

    private static String TAG = "lifeCycle";
    ActivityResultLauncher<String> requestPhonePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if(result)
            {

                startServiceButton(null);            }
            }



    });

    private ActivityResultLauncher<String[]> requestLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                if (result != null) {
                    boolean fine = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    boolean coarse = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                    // this means permission has been approved
                    if (fine && coarse) {
                        // this method handles locations
                        startLocationForegroundService(null);
                    Toast.makeText(MainActivity.this, "Location Permission approved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Location Service cannot work without location approval", Toast.LENGTH_SHORT).show();
                    }
                }
            });




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");


    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this,MusicService.class);
        stopService(intent);

        Log.d(TAG, "onDestroy: ");
    }


    public void moveToSecond(View view) {
        Intent intent = new Intent(this,SecondActivity.class);
        startActivity(intent);
    }

    public void stopBackService(View view) {
        Intent intent = new Intent(this,MusicService.class);
        stopService(intent);
    }

    public void startServiceButton(View view)
    {
        if ( ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)
            requestPhonePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
        else {
            Intent intent = new Intent(this, MusicService.class);
            startService(intent);
        }
    }

    public void startMusicThread(View view)
    {

         new Thread(new Runnable() {
             @Override
             public void run() {

                 MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.music);
                 mediaPlayer.start();
                 mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                     @Override
                     public boolean onError(MediaPlayer mp, int what, int extra) {
                         Log.d(TAG, "onError: what = " + what +" , extra = " + extra);
                         return false;
                     }
                 });


                 /*
                 for (int i = 0; i < 150; i++) {
                     try {
                         Thread.sleep(500);
                     } catch (InterruptedException e) {
                         Log.d(TAG, "run: catch");
                     }
                     Log.d(TAG, "run: " + i);
                 }

                  */
             }

         }).start();
    }

    public void startTimerServiceNoThread(View view)
    {
        Intent intent = new Intent(this, TimerOnlyService.class);
        startService(intent);

    }

    public void startTimerServiceWithThread(View view)
    {
        Intent intent = new Intent(this, TimerServiceWithThread.class);
        startService(intent);

    }


    // needs location permission
    public void startLocationForegroundService(View view)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
            requestLocationPermissionLauncher.launch(permissions);

        }
        else {

            Intent intent = new Intent(this, MyLocationService.class);
            ContextCompat.startForegroundService(this, intent);
        }
    }

    public void stopForegroundLocationService(View view) {
        Intent intent = new Intent(this, MyLocationService.class);
        stopService(intent);

    }
}