package com.example.lifecycleactivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.BlockingDeque;

public class TimerServiceWithThread extends Service {
    private static final String TAG = "Timer Service with Thread";

    public TimerServiceWithThread() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
                
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onStartCommand: start sleep");
                for (int i= 0; i < 10; i++) {
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "onStartCommand: loop  " + i);


                }
            }
        }).start();


        Log.d(TAG, "onStartCommand: end sleep");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}