package com.example.padelwear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class Jugadores extends Activity {
    private static final int SPEECH_REQUEST_CODE = 123;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jugadores);
        View.OnClickListener onClickNombre = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView = (TextView) v;
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(intent, SPEECH_REQUEST_CODE);
            }
        };
        findViewById(R.id.pareja).setOnClickListener(onClickNombre);
        findViewById(R.id.rival_1).setOnClickListener(onClickNombre);
        findViewById(R.id.rival_2).setOnClickListener(onClickNombre);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent datos) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = datos.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String textoReconocido = results.get(0);
            textView.setText(textoReconocido);
        }
        super.onActivityResult(requestCode, resultCode, datos);
    }
}