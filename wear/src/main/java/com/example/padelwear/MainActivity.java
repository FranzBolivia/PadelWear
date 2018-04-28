package com.example.padelwear;


import android.os.Bundle;

import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;

import android.support.wearable.activity.WearableActivity;
import android.content.Intent;

import android.view.View;

import android.widget.Toast;

public class MainActivity extends WearableActivity {
    String[] elementos = {"Partida", "Terminar partida", "Historial",
            "Notificación", "Pasos", "Pulsaciones", "Terminar partida","Dismiss"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WearableRecyclerView lista = (WearableRecyclerView) findViewById(R.id.lista);
        lista.setEdgeItemsCenteringEnabled(true);
        lista.setLayoutManager(new WearableLinearLayoutManager(this,
                new CustomLayoutCallback()));

        Adaptador adaptador = new Adaptador(this, elementos);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Integer tag = (Integer) v.getTag();
                                                 switch (tag) {
                                                     case 1:
                                                         startActivity(new Intent(MainActivity.this, Confirmacion.class));
                                                         break;
                                                     case 2:
                                                         startActivity(new Intent(MainActivity.this, Historial.class));
                                                         break;
                                                     case 4:
                                                         startActivity(new Intent(MainActivity.this, Pasos.class));
                                                         break;
                                                     case 7:
                                                         startActivity(new Intent(MainActivity.this, SwipeDismiss.class));
                                                         break;

                                                 }
                                             }
                                         }
        );
        lista.setAdapter(adaptador);
        lista.setCircularScrollingGestureEnabled(true);
        lista.setScrollDegreesPerScreen(180);
        lista.setBezelFraction(0.5f);
    }
}
