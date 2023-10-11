package id.flutter.flutter_background_service;

import static android.content.Context.ALARM_SERVICE;
import static android.os.Build.VERSION.SDK_INT;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.AlarmManagerCompat;
import androidx.core.content.ContextCompat;

public class WatchdogReceiver extends BroadcastReceiver {
    private static final int QUEUE_REQUEST_ID = 111;
    private static final String ACTION_RESPAWN = "id.flutter.background_service.RESPAWN";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_RESPAWN)) {
            final Config config = new Config(context);
            boolean isRunning = false;

            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (BackgroundService.class.getName().equals(service.service.getClassName())) {
                    isRunning = true;
                }
            }

            if (!config.isManuallyStopped() && !isRunning) {
                if (config.isForeground()) {
                    ContextCompat.startForegroundService(context, new Intent(context, BackgroundService.class));
                } else {
                    context.startService(new Intent(context, BackgroundService.class));
                }
            }
        }
    }
}