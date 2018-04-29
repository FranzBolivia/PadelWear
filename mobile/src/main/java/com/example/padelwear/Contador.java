package com.example.padelwear;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.comun.DireccionesGestureDetector;
import com.example.comun.Partida;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by daniel on 22/05/2017.
 */
public class Contador extends Activity {
    public Partida partida;
    private TextView misPuntos, misJuegos, misSets,
            susPuntos, susJuegos, susSets;

    private TextView hora;

    private Vibrator vibrador;
    private long[] vibrEntrada = {01, 500};
    private long[] vibrDeshacer = {01, 500, 500, 500};
    private static final String WEAR_ARRANCAR_ACTIVIDAD = "/arrancar_actividad";
    private GoogleApiClient apiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contador_movil);




         partida=new Partida();

        vibrador = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        misPuntos = (TextView) findViewById(R.id.misPuntos);
        susPuntos = (TextView) findViewById(R.id.susPuntos);
        misJuegos = (TextView) findViewById(R.id.misJuegos);
        susJuegos = (TextView) findViewById(R.id.susJuegos);
        misSets = (TextView) findViewById(R.id.misSets);
        susSets = (TextView) findViewById(R.id.susSets);

        hora = (TextView) findViewById(R.id.hora);

         actualizaNumeros();

        View fondo = findViewById(R.id.fondo);
        fondo.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector detector = new DireccionesGestureDetector(Contador.this, new DireccionesGestureDetector.SimpleOnDireccionesGestureListener() {
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

                public void onLongPress(MotionEvent event) {
                    // dismissOverlay.show();
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });

        misPuntos.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector detector = new DireccionesGestureDetector(Contador.this,
                    new DireccionesGestureDetector.SimpleOnDireccionesGestureListener() {
                        @Override
                        public boolean onDerecha(MotionEvent e1, MotionEvent e2, float distX, float distY) {
                            partida.puntoPara(true);
                            vibrador.vibrate(vibrEntrada, -1);

                            actualizaNumeros();
                            return true;
                        }

                        public void onLongPress(MotionEvent event) {
                            //  dismissOverlay.show();
                        }
                    });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }

            public void onLongPress(MotionEvent event) {
                //  dismissOverlay.show();
            }
        });

        susPuntos.setOnTouchListener(new View.OnTouchListener() {

            GestureDetector detector = new DireccionesGestureDetector(Contador.this,
                    new DireccionesGestureDetector.SimpleOnDireccionesGestureListener() {
                        @Override
                        public boolean onDerecha(MotionEvent e1, MotionEvent e2, float distX, float distY) {
                            partida.puntoPara(false);
                            vibrador.vibrate(vibrEntrada, -1);

                            actualizaNumeros();
                            return true;
                        }

                        public void onLongPress(MotionEvent event) {
                            //  dismissOverlay.show();
                        }
                    });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
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
    protected void onStart() {
        super.onStart();
        //   apiClient.connect();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }
    private void mandarMensaje(final String path, final String texto) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodos = Wearable.NodeApi.getConnectedNodes(apiClient).await();

                for (Node nodo : nodos.getNodes()) {
                    Wearable.MessageApi.sendMessage(apiClient, nodo.getId(), path, texto.getBytes())
                            .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(@NonNull MessageApi.SendMessageResult resultado) {
                                    if (!resultado.getStatus().isSuccess()) {
                                        Log.e("sincronizacion", "Error al mandar mensaje. Código:" + resultado.getStatus().getStatusCode());
                                    } else {

                                        Log.d("ABRIR_WEAR", "APP OPEN WEAR");
                                    }
                                }
                            });
                }

            }
        }).start();
    }


    // Sincronizar datos desde el móvil al reloj

}