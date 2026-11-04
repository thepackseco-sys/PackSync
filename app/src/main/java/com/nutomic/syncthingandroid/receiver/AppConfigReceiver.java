package com.nutomic.syncthingandroid.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.nutomic.syncthingandroid.service.Constants;


import static com.nutomic.syncthingandroid.service.RunConditionMonitor.ACTION_UPDATE_SHOULDRUN_DECISION;

/**
 * Broadcast-receiver to control and configure Syncthing remotely.
 */
public class AppConfigReceiver extends BroadcastReceiver {

    private static final String TAG = "AppConfigReceiver";

    /**
     * Let Syncthing-Service follow run conditions
     */
    private static final String ACTION_FOLLOW = ".action.FOLLOW";

    /**
     * Start the Syncthing-Service
     */
    private static final String ACTION_START = ".action.START";

    /**
     * Stop the Syncthing-Service
     * Records the existing force-stop choice for the run-condition monitor.
     * This action remains gated by the opt-in broadcast-control preference.
     */
    private static final String ACTION_STOP  = ".action.STOP";


    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = AppActionPolicy.suffix(context.getPackageName(),
                intent == null ? null : intent.getAction());
        if (intentAction == null) return;
        if (!getPrefBroadcastServiceControl(context)) {
            switch (intentAction) {
                case ACTION_FOLLOW:
                case ACTION_START:
                case ACTION_STOP:
                    Log.w(TAG, "Ignored intent action \"" + intentAction +
                                "\". Enable Settings > Experimental > Service Control by Broadcast if you like to control syncthing remotely.");
                    break;
            }
            return;
        }

        switch (intentAction) {
            case ACTION_FOLLOW:
                Log.d(TAG, "followRunConditions by intent");
                setPrefBtnStateForceStartStopAndNotify(context, Constants.BTNSTATE_NO_FORCE_START_STOP);
                requestServiceStart(context);
                break;
            case ACTION_START:
                Log.d(TAG, "forceStart by intent");
                setPrefBtnStateForceStartStopAndNotify(context, Constants.BTNSTATE_FORCE_START);
                requestServiceStart(context);
                break;
            case ACTION_STOP:
                Log.d(TAG, "forceStop by intent");
                setPrefBtnStateForceStartStopAndNotify(context, Constants.BTNSTATE_FORCE_STOP);
                break;
            default:
                Log.w(TAG, "invalid intent action: " + intentAction);
        }
    }

    private static void requestServiceStart(Context context) {
        try {
            BootReceiver.startServiceCompat(context);
        } catch (IllegalStateException | SecurityException refused) {
            Log.w(TAG, "Android refused background start; saved run-condition choice retained");
        }
    }

    private static boolean getPrefBroadcastServiceControl(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Constants.PREF_BROADCAST_SERVICE_CONTROL, false);
    }

    private static boolean getPrefStartServiceOnBoot(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Constants.PREF_START_SERVICE_ON_BOOT, true);
    }

    private static void setPrefBtnStateForceStartStopAndNotify (final Context context, 
                                                                        final Integer newState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.PREF_BTNSTATE_FORCE_START_STOP, newState);
        editor.apply();

        // Notify {@link RunConditionMonitor} that the button's state changed.
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_UPDATE_SHOULDRUN_DECISION);
        localBroadcastManager.sendBroadcast(intent);
    }
}
