package com.example.padelwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;

public class WearReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MainActivity.ACTION_DEMAND)) {
            String extras = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
            Log.v("Notificaciones", "Se recibe ACTION_DEMAND; extras = " + extras);
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            CharSequence reply = remoteInput.getCharSequence(MainActivity.EXTRA_RESPUESTA_POR_VOZ);
            Log.v("Notificaciones", "Respuesta dictada desde el wearable: " + reply);
        }
    }
}
