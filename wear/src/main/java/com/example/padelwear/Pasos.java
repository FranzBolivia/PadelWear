package com.example.padelwear;

import android.app.FragmentManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.CardFragment;

public class Pasos extends WearableActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_tarjeta);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        CardFragment cardFragment = CardFragment.create("Pasos:",
                "911 en 13 minutos", R.drawable.pasos);
        fragmentTransaction.add(R.id.contenedor, cardFragment);
        fragmentTransaction.commit();
    }
}