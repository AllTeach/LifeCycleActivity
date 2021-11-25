package com.example.lifecycleactivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MusicService extends Service {

    private static final String TAG = "Music Service";
    private MediaPlayer mediaPlayer;
    private boolean playing = false;
    private boolean paused = false;

     private MyPhoneReceiver phoneReceiver;

    public class MyPhoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            Toast.makeText(context, "Receiver", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onReceive: insode receiver onReceive ");
            // stop the service
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                if(mediaPlayer !=null) {
                    mediaPlayer.pause();
                    paused = true;
                    Log.d(TAG, "onReceive: insode receiver pause media ");
                }
            }

            else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            {
                if(mediaPlayer !=null) {

                    if(paused) {
                        mediaPlayer.start();
                        paused=false;
                        Log.d(TAG, "onReceive: insode receiver pause media ");
                    }
                }

            }



        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        phoneReceiver = new MyPhoneReceiver();

        IntentFilter intentFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(phoneReceiver, intentFilter);

        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(!playing) {

            mediaPlayer = MediaPlayer.create(MusicService.this, R.raw.music);
            mediaPlayer.start();
            playing = true;
            // loop
            mediaPlayer.setLooping(true);
        }
        return START_STICKY;
    }

    public MusicService()
    {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if(phoneReceiver!=null)
            unregisterReceiver(phoneReceiver);
    }
}