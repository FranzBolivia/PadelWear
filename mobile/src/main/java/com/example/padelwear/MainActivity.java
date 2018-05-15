package com.example.padelwear;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.NotificationChannel.DEFAULT_CHANNEL_ID;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    private static final String WEAR_ARRANCAR_ACTIVIDAD = "/arrancar_actividad";
    private static final String WEAR_MANDAR_TEXTO = "/mandar_texto";
    private GoogleApiClient apiClient;
    private static final String ITEM_FOTO = "/item_foto"; private static final String ASSET_FOTO = "/asset_foto";
    //Notific

    NotificationManager notificationManager,manager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1001;
    final static String MI_GRUPO_DE_NOTIFIC = "mi_grupo_de_notific";
    public static final String EXTRA_RESPUESTA_POR_VOZ = "extra_respuesta_por_voz";
    public static final String EXTRA_MESSAGE="com.example.notificaciones.EXTRA_MESSAGE";
    public static final String ACTION_DEMAND="com.example.notificaciones.ACTION_DEMAND";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        apiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(this).build();



//Miramos si hemos recibido una respuesta por voz
        Bundle respuesta = RemoteInput.getResultsFromIntent(getIntent());
        if (respuesta != null) {
            CharSequence texto = respuesta.getCharSequence(EXTRA_RESPUESTA_POR_VOZ);
            ((TextView) findViewById(R.id.textViewRespuesta)).setText(texto);
        }

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CANAL_ID, "Padel", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Descripcion del canal");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 100, 300, 100});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel( notificationChannel);
        }
        Button wearButton = (Button) findViewById(R.id.boton1);
        wearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Seguimiento","ENtro en Onclick");

                // Creamos un BigTextStyle para la segunda página
                NotificationCompat.BigTextStyle segundaPg = new NotificationCompat.BigTextStyle();
                segundaPg.setBigContentTitle("Página 2").bigText("Más texto Pagina2.");
                NotificationCompat.BigTextStyle terceraPg = new NotificationCompat.BigTextStyle();
                segundaPg.setBigContentTitle("Página 3").bigText("Más texto. pagina 3");


                // Creamos una notification para la segunda página
                Notification notificacionPg2 = new NotificationCompat.Builder(MainActivity.this).setStyle(segundaPg).build();

                List<Notification> paginas = new ArrayList<Notification>();
                paginas.add(notificacionPg2);
                paginas.add(new NotificationCompat.Builder(MainActivity.this).setStyle(terceraPg).build());
                paginas.add(notificacionPg2);

                String s = "Texto largo con descripción detallada de la notificación. ";

                Intent intencionMapa = new Intent(MainActivity.this, MainActivity.class);
                PendingIntent intencionPendienteMapa = PendingIntent.getActivity(MainActivity.this, 0, intencionMapa, 0);
                Intent intencionLlamar = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:555123456"));
                PendingIntent intencionPendienteLlamar = PendingIntent.getActivity(MainActivity.this, 0, intencionLlamar, 0);

                NotificationCompat.Action accion = new NotificationCompat.Action.Builder(R.mipmap.ic_action_call, "llamar Wear", intencionPendienteLlamar).build();

                List<NotificationCompat.Action> acciones = new ArrayList<NotificationCompat.Action>();
                acciones.add(accion);
                acciones.add(new NotificationCompat.Action(R.mipmap.ic_action_locate, "Ver mapa", intencionPendienteMapa));


                NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.escudo_upv)).
                                addActions(acciones)
                        .addPages(paginas);


                NotificationCompat.Builder notificacion = new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Título")
                        .setContentText(Html.fromHtml("<b>Notificación</b> <u>Android <i>Wear</i></u>"))
                        .addAction(R.mipmap.ic_action_call, "llamar", intencionPendienteLlamar)
                        //Pone el Icono ya no aparece en la notificacion del movil pero si en wear
                        // /.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.escudo_upv))
                        // AL crear el extender esta linea deja de funcionar
                        // .extend(new NotificationCompat.WearableExtender().addActions(acciones))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(s + s + s + s))
                        .extend(wearableExtender)
                        .setGroup(MI_GRUPO_DE_NOTIFIC)
                        .setAutoCancel(true);

                //       .setContentIntent(intencionPendienteMapa);;
                notificationManager.notify(NOTIFICACION_ID, notificacion.build());







                int idNotificacion2 = 002;
                NotificationCompat.Builder notificacion2 = new NotificationCompat.Builder(MainActivity.this, CANAL_ID).setContentTitle("Nueva Conferencia").setContentText("Los neutrinos").setSmallIcon(R.mipmap.ic_action_mail_add).setGroup(MI_GRUPO_DE_NOTIFIC);
                notificationManager.notify(idNotificacion2, notificacion2.build());


                // Creamos una notificacion resumen
                int idNotificacion3 = 003;
                NotificationCompat.Builder notificacion3 = new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                        .setContentTitle("2 notificaciones UPV")
                        .setSmallIcon(R.mipmap.ic_action_attach)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.escudo_upv))
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("Nueva Conferencia Los neutrinos")
                                .addLine("Nuevo curso Wear OS")
                                .setBigContentTitle("2 notificaciones UPV")
                                .setSummaryText("info@upv.es")).setNumber(2)
                        .setGroup(MI_GRUPO_DE_NOTIFIC).setGroupSummary(true);
                notificationManager.notify(idNotificacion3, notificacion3.build());

            }
        });

        Button butonVoz = (Button) findViewById(R.id.boton_voz);
        butonVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creamos una intención de respuesta
                Intent intencion = new Intent(MainActivity.this, MainActivity.class);
                PendingIntent intencionPendiente = PendingIntent.getActivity(MainActivity.this, 0, intencion, PendingIntent.FLAG_UPDATE_CURRENT);
                //Creamos entrada remota para añadir la accion
                String[] opcRespuesta = getResources().getStringArray(R.array.opciones_respuesta);
                // Creamos la entrada remota para añadirla a la acción
                RemoteInput entradaRemota = new RemoteInput.Builder(EXTRA_RESPUESTA_POR_VOZ).setLabel("respuesta por voz").setChoices(opcRespuesta).build();
                // Creamos la acción
                NotificationCompat.Action accion = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_set_as, "responder", intencionPendiente)
                        .addRemoteInput(entradaRemota).build();
                // Creamos la notificación
                int idNotificacion = 002;
                NotificationCompat.Builder notificacion4 = new NotificationCompat.Builder(MainActivity.this, CANAL_ID).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Respuesta por Voz").setContentText("Indica una respuesta").extend(new NotificationCompat.WearableExtender().addAction(accion));
                // Lanzamos la notificación
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);

                notificationManager.notify(idNotificacion, notificacion4.build());
            }
        });

        Button wearButton2 = (Button) findViewById(R.id.boton_broadcast);
        wearButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(MainActivity.this, WearReceiver.class).putExtra(EXTRA_MESSAGE, "alguna información relevante")
                        .setAction(ACTION_DEMAND);
                PendingIntent intencionPendiente = PendingIntent.getBroadcast(MainActivity.this, 0, intencion, 0);
            }

        });



    }

    private void mandarFoto() {
        Intent intencion = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intencion.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intencion, 1234);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent datos) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {

            Log.i("Mandar foto","LLego al ActivResult");
            Bundle extras = datos.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            Asset asset = createAssetFromBitmap(bitmap);
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create(ITEM_FOTO);
            putDataMapReq.getDataMap().putAsset(ASSET_FOTO, asset);
            PutDataRequest request = putDataMapReq.asPutDataRequest();
            Wearable.getDataClient(getApplicationContext()).putDataItem(request);
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }





    @Override
    public void onMessageReceived(final MessageEvent mensaje) {
        if (mensaje.getPath().equalsIgnoreCase(WEAR_MANDAR_TEXTO)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  //  textView.setText(textView.getText() + "\n" + new String(mensaje.getData()));
                    Log.i ("sincronizacion","Llego el mensaje"+new String(mensaje.getData()));
                }
            });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(apiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    protected void onStop() {
        Wearable.MessageApi.removeListener(apiClient, this);
        if (apiClient != null && apiClient.isConnected()) {
            apiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.accion_contador) {
            startActivity(new Intent(this, Contador.class));
            return true;
        }


        if (id == R.id.accion_fotografia) {
            mandarFoto();


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
