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

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class DetallesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);


        LlenarTablaConDatos("Ruta","Tarifa Normal","Tarifa Adulto Mayor","#000000");

        ArrayList<String> lista = (ArrayList<String> ) getIntent().getSerializableExtra("Lista");
        String[] separar;
        int contador=0;
        String ruta="";
        String tarifaNormal="";
        String tarifaAdulto="";
        for(String resultado: lista){
            separar=  resultado.split(",");
            for(String r: separar){
                if(contador==0){
                    ruta=r;

                }
                if(contador==1){
                    tarifaNormal=r;

                }
                if(contador==2){

                    tarifaAdulto=r;

                }
                contador=contador+1;


            }
            contador=0;
            LlenarTablaConDatos(ruta,tarifaNormal,tarifaAdulto,"#0594D3");


        }






    }



    public void LlenarTablaConDatos(String ruta, String tarifaNormal,String tarifaAdulto, String colores){

        TableLayout lista = (TableLayout)findViewById(R.id.tablaDatos);
        String [] cadena = {ruta,tarifaNormal,tarifaAdulto,colores};
        TableRow row = new TableRow(getBaseContext());

        TextView textView;
        for(int i=0; i<3 ;i++){

            textView= new TextView(getBaseContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setPadding(20,20,20,20);
            textView.setBackgroundResource(R.color.colorPrimary);
            textView.setText(cadena[i]);
            textView.setTextColor(Color.parseColor(colores));

            row.addView(textView);


        }

        lista.addView(row);

    }
    public  void Atras(View vista){

super.onBackPressed();

    }


}
