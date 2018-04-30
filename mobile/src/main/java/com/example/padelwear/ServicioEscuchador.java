package com.example.padelwear;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ServicioEscuchador extends WearableListenerService {
    private static final String WEAR_ARRANCAR_ACTIVIDAD = "/arrancar_actividad";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equalsIgnoreCase(WEAR_ARRANCAR_ACTIVIDAD)) {
            Intent intent = new Intent(this, Contador.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}