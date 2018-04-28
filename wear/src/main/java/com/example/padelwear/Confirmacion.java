package com.example.padelwear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Confirmacion extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmacion);
        ImageButton aceptar = (ImageButton) findViewById(R.id.aceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivityForResult(new Intent(Confirmacion.this, CuentaAtras.class), 9);
            }
        });
        ImageButton cancelar = (ImageButton) findViewById(R.id.cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
//            Toast.makeText(this, "Accion cancelada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Confirmacion.this, ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.FAILURE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Operación cancelada");
            startActivity(intent);

        } else if (resultCode == RESULT_OK) {

            Intent intent = new Intent(Confirmacion.this, ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Operación aceptada");
            startActivity(intent);
            //Toast.makeText(this, "Accion aceptada", Toast.LENGTH_SHORT).show(); //Guardamos datos de partida finish();

        }
    }
}