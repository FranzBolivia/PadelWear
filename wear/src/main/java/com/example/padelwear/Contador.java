package com.example.padelwear;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.comun.DireccionesGestureDetector;
import com.example.comun.Partida;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Contador extends WearableActivity implements DataClient.OnDataChangedListener {
    private Partida partida;
    private TextView misPuntos, misJuegos, misSets, susPuntos, susJuegos, susSets, hora;
    private Vibrator vibrador;
    private long[] vibrEntrada = {0l, 500};
    private long[] vibrDeshacer = {0l, 500, 500, 500};
    private DismissOverlayView dismissOverlay;

    private Typeface fuenteNormal = Typeface.create("sans-serif", Typeface.NORMAL);
    private Typeface fuenteFina = Typeface.create("sans-serif-thin", Typeface.NORMAL);
    private static final String WEAR_ARRANCAR_ACTIVIDAD = "/arrancar_actividad";
    private static final String WEAR_MANDAR_TEXTO = "/mandar_texto";
    private GoogleApiClient apiClient;
    //Variables para la actualizacion
    private static final String WEAR_PUNTUACION = "/puntuacion";
    private static final String KEY_MIS_PUNTOS = "com.example.padel.key.mis_puntos";
    private static final String KEY_MIS_JUEGOS = "com.example.padel.key.mis_juegos";
    private static final String KEY_MIS_SETS = "com.example.padel.key.mis_sets";
    private static final String KEY_SUS_PUNTOS = "com.example.padel.key.sus_puntos";
    private static final String KEY_SUS_JUEGOS = "com.example.padel.key.sus_juegos";
    private static final String KEY_SUS_SETS = "com.example.padel.key.sus_sets";
//    private static final String WEAR_PUNTUACION = "/puntuacion";

    private static final String ITEM_FOTO = "/item_foto";
    private static final String ASSET_FOTO = "/asset_foto";

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
        hora = (TextView) findViewById(R.id.hora);
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

        apiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).build();

        mandarMensaje(WEAR_ARRANCAR_ACTIVIDAD, "");


    }


    private void sincronizaDatos() {
        Log.d("Padel Wear", "Sincronizando");
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(WEAR_PUNTUACION);
        putDataMapReq.getDataMap().putByte(KEY_MIS_PUNTOS, partida.getMisPuntosByte());
        putDataMapReq.getDataMap().putByte(KEY_MIS_JUEGOS, partida.getMisJuegosByte());
        putDataMapReq.getDataMap().putByte(KEY_MIS_SETS, partida.getMisSetsByte());
        putDataMapReq.getDataMap().putByte(KEY_SUS_PUNTOS, partida.getSusPuntosByte());
        putDataMapReq.getDataMap().putByte(KEY_SUS_JUEGOS, partida.getSusJuegosByte());
        putDataMapReq.getDataMap().putByte(KEY_SUS_SETS, partida.getSusSetsByte());

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();

        Wearable.getDataClient(getApplicationContext()).putDataItem(putDataReq);


    }


    private void mandarMensaje(final String path, final String texto) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("sincronizacion", "Entro");
                NodeApi.GetConnectedNodesResult nodos = Wearable.NodeApi.getConnectedNodes(apiClient).await();
                for (Node nodo : nodos.getNodes()) {
                    Wearable.MessageApi.sendMessage(apiClient, nodo.getId(), path, texto.getBytes()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult resultado) {
                            Log.i("sincronizacion", resultado.getStatus().getStatusMessage());
                            if (!resultado.getStatus().isSuccess()) {
                                Log.e("sincronizacion", "Error al mandar mensaje. Código:" + resultado.getStatus().getStatusCode());
                            }
                        }
                    });
                }
            }
        }).start();
    }


    @Override
    public void onDataChanged(DataEventBuffer eventos) {
        Log.i("MAndar foto", "data change");
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

                } else if (item.getUri().getPath().equals(ITEM_FOTO)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(item);
                    Asset asset = dataMapItem.getDataMap().getAsset(ASSET_FOTO);
                    LoadBitmapFromAsset tarea = new LoadBitmapFromAsset();
                    tarea.execute(asset);
                }

            } else if (evento.getType() == DataEvent.TYPE_DELETED) { // Algún ítem ha sido borrado
            }
        }
    }


    class LoadBitmapFromAsset extends AsyncTask<Asset, Void, Bitmap> {
        private static final int TIMEOUT_MS = 2000;

        @Override
        protected Bitmap doInBackground(Asset... assets) {
            if (assets.length < 1 || assets[0] == null) {
                throw new IllegalArgumentException("El asset no puede ser null");
            }

            InputStream assetInputStream = null;
            try {
                assetInputStream = Tasks.await(Wearable.getDataClient(getApplicationContext())
                        .getFdForAsset(assets[0])).getInputStream();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (assetInputStream == null) {
                Log.w("Sincronización", "Asset desconocido");
                return null;
            } // decodificamos el Stream en un Bitmap
            return BitmapFactory.decodeStream(assetInputStream);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ((FrameLayout) findViewById(R.id.boxInsert)).setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    protected void onStop() {
        if (apiClient != null && apiClient.isConnected()) {
            apiClient.disconnect();
        }
        super.onStop();
    }


    void actualizaNumeros() {
        misPuntos.setText(partida.getMisPuntos());
        susPuntos.setText(partida.getSusPuntos());
        misJuegos.setText(partida.getMisJuegos());
        susJuegos.setText(partida.getSusJuegos());
        misSets.setText(partida.getMisSets());
        susSets.setText(partida.getSusSets());
        sincronizaDatos();
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

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        hora.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));

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
}
