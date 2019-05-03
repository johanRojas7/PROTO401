package com.costa.myapplication;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import route.costa.myapplication.R;


public class DetallesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);


        LlenarTablaConDatos("Ruta","Tarifa Normal","Tarifa Adulto Mayor","#FFFFFF");

        ArrayList<String> lista = (ArrayList<String> ) getIntent().getSerializableExtra("Lista");
        String[] separar;
        int contador=0;
        String ruta="";
        String tarifaNormal="";
        String tarifaAdulto="";
        String color="";
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

                if(contador==3){

                    color=r;

                }
                contador=contador+1;


            }
            contador=0;
            LlenarTablaConDatos(ruta,tarifaNormal,tarifaAdulto,color);


        }






    }



    public void LlenarTablaConDatos(String ruta, String tarifaNormal,String tarifaAdulto, String colores){

        TableLayout lista = (TableLayout)findViewById(R.id.tablaDatos);
        String [] cadena = {ruta,"₡ "+tarifaNormal,"₡ "+tarifaAdulto,colores};
        TableRow row = new TableRow(getBaseContext());

        TextView textView;
        for(int i=0; i<3 ;i++){

            textView= new TextView(getBaseContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setPadding(20,20,20,20);
            textView.setBackgroundResource(R.drawable.detalles);

            textView.setText(cadena[i]);
            textView.setTextColor(Color.parseColor(colores));

            row.addView(textView);


        }

        lista.addView(row);

    }
    public  void Atras(View vista){
        ObtenerTiempoDeLlegada(9.9325427,-84.0795782,10.6266455412967,-85.44377561617,"now");
super.onBackPressed();



    }


    public void app(View vista){



    }

    public void ObtenerTiempoDeLlegada(Double latitudSalida, Double longitudSalida, Double latitudLLegada, Double longitudLLegada, String fechayHora){
        String latitudOrigen=Double.toString(latitudSalida);
        String longitudOrigen=Double.toString(longitudSalida);
        String latitudDestino=Double.toString(latitudLLegada);
        String longitudDestino=Double.toString(longitudLLegada);
        String horaDeSalida=fechayHora;
        String url ="https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origin="+latitudOrigen+","+longitudOrigen+"&destination="+latitudDestino+","+longitudDestino+"& departure_time="+horaDeSalida +"&traffic_model=best_guess&key=AIzaSyDCYfnub3jihOu0bZw2c3qxRUy-cRqwBrc";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



                try {
                    JSONObject jso = new JSONObject(response);
                    ObtenerTiempoAproximado(jso);
                    Log.i("jsonRuta: ",""+response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);



    }

    private void ObtenerTiempoAproximado(JSONObject jso) {

        JSONArray jRoutes;

     JSONObject jj= jso;

        try {
            jRoutes = jso.getJSONArray("text");
            for (int i=0; i<jRoutes.length();i++){



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
