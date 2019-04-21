package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static java.security.AccessController.getContext;

public class DetallesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);






    }

    public void hola(View v){
        TableLayout lista = (TableLayout)findViewById(R.id.tablaDatos);
        String [] cadena = {"Color de ruta","Tarifa adulto"};
        TableRow row = new TableRow(getBaseContext());

        TextView textView;
        for(int i=0; i<2 ;i++){

            textView= new TextView(getBaseContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setPadding(20,20,20,20);
            textView.setBackgroundResource(R.color.colorPrimary);
            textView.setText(cadena[1]);
            textView.setTextColor(Color.BLUE);
            row.addView(textView);


        }

        lista.addView(row);



    }


    public  void Atras(View vista){

super.onBackPressed();

    }


}
