package com.example.padelwear;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DismissOverlayView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.comun.DireccionesGestureDetector;
import com.example.comun.Partida;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;
import java.util.Date;

public class Contador extends WearableActivity {
    private Partida partida;
    private TextView misPuntos, misJuegos, misSets, susPuntos, susJuegos, susSets,hora;
    private Vibrator vibrador;
    private long[] vibrEntrada = {0l, 500};
    private long[] vibrDeshacer = {0l, 500, 500, 500};
    private DismissOverlayView dismissOverlay;

    private Typeface fuenteNormal = Typeface.create("sans-serif", Typeface.NORMAL);
    private Typeface fuenteFina = Typeface.create("sans-serif-thin", Typeface.NORMAL);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contador);
        setAmbientEnabled();
// Para evitar que entre en suspension
        //    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        partida = new Partida();
        vibrador = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        misPuntos = (TextView) findViewById(R.id.misPuntos);
        susPuntos = (TextView) findViewById(R.id.susPuntos);
        misJuegos = (TextView) findViewById(R.id.misJuegos);
        susJuegos = (TextView) findViewById(R.id.susJuegos);
        misSets = (TextView) findViewById(R.id.misSets);
        susSets = (TextView) findViewById(R.id.susSets);
hora=(TextView) findViewById(R.id.hora);
        dismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        dismissOverlay.setIntroText("Para salir de la aplicación, haz una pulsación larga");
        dismissOverlay.showIntroIfNecessary();

        actualizaNumeros();
        View fondo = findViewById(R.id.fondo);
        fondo.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector detector = new DireccionesGestureDetector(
                    Contador.this, new DireccionesGestureDetector.SimpleOnDireccionesGestureListener()

            {
                @Override
                public void onLongPress(MotionEvent e) {
                    dismissOverlay.show();
                }

                @Override
                public boolean onArriba(MotionEvent e1, MotionEvent e2, float distX, float distY) {
                    partida.rehacerPunto();
                    vibrador.vibrate(vibrDeshacer, -1);
                    actualizaNumeros();
                    return true;
                }

                @Override
                public boolean onAbajo(MotionEvent e1, MotionEvent e2, float distX, float distY) {
                    partida.deshacerPunto();
                    vibrador.vibrate(vibrDeshacer, -1);
                    actualizaNumeros();
                    return true;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent evento) {
                detector.onTouchEvent(evento);
                return true;
            }
        });
        misPuntos.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector detector = new DireccionesGestureDetector(Contador.this, new DireccionesGestureDetector.SimpleOnDireccionesGestureListener() {

                @Override
                public void onLongPress(MotionEvent e) {
                    dismissOverlay.show();
                }

                @Override
                public boolean onDerecha(MotionEvent e1, MotionEvent e2, float distX, float distY) {
                    partida.puntoPara(true);
                    vibrador.vibrate(vibrEntrada, -1);
                    actualizaNumeros();
                    return true;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent evento) {
                detector.onTouchEvent(evento);
                return true;
            }
        });
        susPuntos.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector detector = new DireccionesGestureDetector(Contador.this, new DireccionesGestureDetector.SimpleOnDireccionesGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    dismissOverlay.show();
                }

                @Override
                public boolean onDerecha(MotionEvent e1, MotionEvent e2, float distX, float distY) {
                    partida.puntoPara(false);
                    vibrador.vibrate(vibrEntrada, -1);
                    actualizaNumeros();
                    return true;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent evento) {
                detector.onTouchEvent(evento);
                return true;
            }
        });
    }

    void actualizaNumeros() {
        misPuntos.setText(partida.getMisPuntos());
        susPuntos.setText(partida.getSusPuntos());
        misJuegos.setText(partida.getMisJuegos());
        susJuegos.setText(partida.getSusJuegos());
        misSets.setText(partida.getMisSets());
        susSets.setText(partida.getSusSets());
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        misPuntos.setTypeface(fuenteFina);
        misPuntos.getPaint().setAntiAlias(false);
        misJuegos.setTypeface(fuenteFina);
        misJuegos.getPaint().setAntiAlias(false);
        misSets.setTypeface(fuenteFina);
        misSets.getPaint().setAntiAlias(false);

        susPuntos.setTypeface(fuenteFina);
        susPuntos.getPaint().setAntiAlias(false);
        susJuegos.setTypeface(fuenteFina);
        susJuegos.getPaint().setAntiAlias(false);
        susSets.setTypeface(fuenteFina);
        susSets.getPaint().setAntiAlias(false);
        hora.setVisibility(View.VISIBLE);
        /*textView.setTextColor(Color.WHITE);
        textView.getPaint().setAntiAlias(false);*/
    }


    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        misPuntos.setTypeface(fuenteNormal);
        misPuntos.getPaint().setAntiAlias(true);
        misJuegos.setTypeface(fuenteNormal);
        misJuegos.getPaint().setAntiAlias(true);
        misSets.setTypeface(fuenteNormal);
        misSets.getPaint().setAntiAlias(true);

        susPuntos.setTypeface(fuenteNormal);
        susPuntos.getPaint().setAntiAlias(true);
        susJuegos.setTypeface(fuenteNormal);
        susJuegos.getPaint().setAntiAlias(true);
        susSets.setTypeface(fuenteNormal);
        susSets.getPaint().setAntiAlias(true);
        hora.setVisibility(View.GONE);

      /*  textView.setTextColor(Color.GREEN);
        textView.getPaint().setAntiAlias(true);*/
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();

        Calendar c = Calendar.getInstance(); c.setTime(new Date()); hora.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));

    }
}
