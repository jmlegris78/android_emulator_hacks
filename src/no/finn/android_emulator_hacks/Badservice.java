package no.finn.android_emulator_hacks;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

public class Badservice extends IntentService {
    private Handler handler = new Handler();
    private PowerManager.WakeLock wakeLock;
    private KeyguardManager.KeyguardLock keyguardLock;
    private WifiManager.WifiLock wifiLock;

    public Badservice() {
        super("Badservice");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Stresstesting has a habbit of turning off the wifi....
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "hack_wifilock");
        wifiLock.acquire();

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("hack_activity");
        keyguardLock.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "hack_wakelock");
        wakeLock.acquire();
        Log.d(HackActivity.TAG, "Badservice running, will hold wifi lock, wakelock and keyguard lock");


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(HackActivity.TAG, "Badservice shutting down");
                wifiLock.release();
                wakeLock.release();
                keyguardLock.reenableKeyguard();
                stopSelf();
            }
        }, 1000 * 60 * 60);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


}
