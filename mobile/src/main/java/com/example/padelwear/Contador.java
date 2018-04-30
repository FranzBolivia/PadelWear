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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
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
public class Contador extends Activity implements DataClient.OnDataChangedListener {
    public Partida partida;
    private TextView misPuntos, misJuegos, misSets,
            susPuntos, susJuegos, susSets;

    private TextView hora;

    private Vibrator vibrador;
    private long[] vibrEntrada = {01, 500};
    private long[] vibrDeshacer = {01, 500, 500, 500};
    private static final String WEAR_ARRANCAR_ACTIVIDAD = "/arrancar_actividad";
    private GoogleApiClient apiClient;
    //Variables para la actualizacion
    private static final String WEAR_PUNTUACION = "/puntuacion";
    private static final String KEY_MIS_PUNTOS = "com.example.padel.key.mis_puntos";
    private static final String KEY_MIS_JUEGOS = "com.example.padel.key.mis_juegos";
    private static final String KEY_MIS_SETS = "com.example.padel.key.mis_sets";
    private static final String KEY_SUS_PUNTOS = "com.example.padel.key.sus_puntos";
    private static final String KEY_SUS_JUEGOS = "com.example.padel.key.sus_juegos";
    private static final String KEY_SUS_SETS = "com.example.padel.key.sus_sets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contador_movil);


        partida = new Partida();

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


        Task<DataItemBuffer> task = Wearable.getDataClient(getApplicationContext()).getDataItems();
        task.addOnCompleteListener(new OnCompleteListener<DataItemBuffer>() {
            @Override
            public void onComplete(@NonNull Task<DataItemBuffer> task) {
                for (DataItem dataItem : task.getResult()) {

                    if (dataItem.getUri().getPath().equals(WEAR_PUNTUACION)) {
                        DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                        final byte mispuntosR = dataMap.getByte(KEY_MIS_PUNTOS);
                        final byte misjuegosR = dataMap.getByte(KEY_MIS_JUEGOS);
                        final byte missetsR = dataMap.getByte(KEY_MIS_SETS);
                        final byte suspuntosR = dataMap.getByte(KEY_SUS_PUNTOS);
                        final byte susjuegosR = dataMap.getByte(KEY_SUS_JUEGOS);
                        final byte sussetsR = dataMap.getByte(KEY_SUS_SETS);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                misPuntos.setText(String.valueOf(mispuntosR));
                                susPuntos.setText(String.valueOf(suspuntosR));
                                misJuegos.setText(String.valueOf(misjuegosR));
                                susJuegos.setText(String.valueOf(susjuegosR));
                                misSets.setText(String.valueOf(missetsR));
                                susSets.setText(String.valueOf(sussetsR));


                            }
                        });
                    }





                }
//                dataItems.release();
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer eventos) {
        for (DataEvent evento : eventos) {
            if (evento.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = evento.getDataItem();
                if (item.getUri().getPath().equals(WEAR_PUNTUACION)) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    final byte mispuntosR = dataMap.getByte(KEY_MIS_PUNTOS);

                    final byte misjuegosR = dataMap.getByte(KEY_MIS_JUEGOS);
                    final byte missetsR = dataMap.getByte(KEY_MIS_SETS);
                    final byte suspuntosR = dataMap.getByte(KEY_SUS_PUNTOS);
                    final byte susjuegosR = dataMap.getByte(KEY_SUS_JUEGOS);
                    final byte sussetsR = dataMap.getByte(KEY_SUS_SETS);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            misPuntos.setText(String.valueOf(mispuntosR));
                            susPuntos.setText(String.valueOf(suspuntosR));
                            misJuegos.setText(String.valueOf(misjuegosR));
                            susJuegos.setText(String.valueOf(susjuegosR));
                            misSets.setText(String.valueOf(missetsR));
                            susSets.setText(String.valueOf(sussetsR));

                            //  ((TextView) findViewById(R.id.textoContador)).setText(Integer.toString(contador));
                        }
                    });
                }
            } else if (evento.getType() == DataEvent.TYPE_DELETED) { // Algún ítem ha sido borrado
            }
        }
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