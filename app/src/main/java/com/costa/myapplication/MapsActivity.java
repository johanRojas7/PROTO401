package com.costa.myapplication;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.PolyUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import route.costa.myapplication.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    public GoogleMap mMap;

    public double latitud;
    public double longitud;
    public double latitudPuntoEscogido=0;
    public double longitudPuntoEscogido=0;

    public double latitudPuntoEscogidoDestino=0;
    public double longitudPuntoEscogidoDestino=0;
    public int contadorDePuntos=1;
   public  List<String> objetoRuta = new ArrayList<String>();
   public ArrayList<String> listaParaDetalles = new ArrayList<String>();

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.INTERNET;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
       getLocationPermission();


        LlenaListaConRutas();

        Places.initialize(getApplicationContext(), "AIzaSyDCYfnub3jihOu0bZw2c3qxRUy-cRqwBrc");

        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS));


        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

              AjustarVistaDelMapaSegunCoordenadas(place.getAddress());

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });




    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }
    @Override
    public void onMapClick(LatLng punto){

        try {

            if(contadorDePuntos==3){

                LimpiarMarcadoresYRutasDeMapa();
                contadorDePuntos=1;
            }

            if(contadorDePuntos==1){
                GenerarMarcadoresEnElMapa("Origen",punto.latitude,punto.longitude);
                latitudPuntoEscogido=punto.latitude;
                longitudPuntoEscogido=punto.longitude;
            }


            if(contadorDePuntos==2){
                latitudPuntoEscogidoDestino=punto.latitude;
                longitudPuntoEscogidoDestino=punto.longitude;
                GenerarMarcadoresEnElMapa("Destino",punto.latitude,punto.longitude);
                TrazarRutasObtenidasEnElMapa(ObtenerRutasDelPunto(objetoRuta));

            }

            contadorDePuntos=contadorDePuntos+1;
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Hubo un error :(", Toast.LENGTH_LONG).show();
        }


    }

    public void LlenaListaConRutas(){
      //  objetoRuta.add("11.210469352249802/-85.61205451379017<11.0730511145966/-85.6319528279177<10.628473892401459/-85.44223100553933");
      //  objetoRuta.add("10.628473892401459/-85.44223100553933<10.301242932408854/-85.83901801528522");
       // objetoRuta.add("11.0730511145966/-85.6319528279177<11.061122708401172/-85.41553022892087");


        // Read the raw csv file
        InputStream is = getResources().openRawResource(R.raw.data);

        // Reads text from character-input stream, buffering characters for efficient reading
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

        // Initialization
        String line = "";

        // Handling exceptions
        try {
            // If buffer is not empty
            while ((line = br.readLine()) != null) {
                // use comma as separator columns of CSV
                String[] cols = line.split(",");
               objetoRuta.add( cols[1]);
                // Print in logcat
               //System.out.println("Coulmn 0 = '" + cols[0] + "', Column 1 = '" + cols[1] + "', Column 2: '" + cols[2] + "'");
            }
        } catch (IOException e) {
            // Prints throwable details
            e.printStackTrace();
        }

    }

    public void BotonLimpiarMapa(View vista){


        LimpiarMarcadoresYRutasDeMapa();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


        mMap.setOnMapClickListener(this::onMapClick);

        LatLng localizacionInicial = new LatLng(10.273563, -84.0739102);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacionInicial,8));

        Toast toast1 = Toast.makeText(getApplicationContext(), "Version Beta", Toast.LENGTH_LONG);
        toast1.show();

    }

    public  void CuandoSePresionaDetalles(View view){




        //Desde  maps activiy hasta detalles
        Intent siguiente = new Intent(MapsActivity.this,DetallesActivity.class);
        siguiente.putStringArrayListExtra("Lista",listaParaDetalles);
        startActivity(siguiente);

    }

    public void AjustarVistaDelMapaSegunCoordenadas(String direccion){
        ConvertirDireccionEnLatitudYLongitud(direccion);
        LatLng coordenadas = new LatLng(latitud, longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas,15));

    }

    private void trazarRuta(JSONObject jso,int rojo,int verde,int azul) {

        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;


        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i=0; i<jRoutes.length();i++){

                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");

                for (int j=0; j<jLegs.length();j++){

                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k<jSteps.length();k++){


                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        Log.i("end",""+polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        mMap.addPolyline(new PolylineOptions().addAll(list).color(Color.argb(255,rojo,verde,azul)).width(8));



                    }


                }



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void GenerarMarcadoresEnElMapa(String titulo,double latitud, double longitud){


        LatLng marcador = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(marcador).title(titulo));

    }

    public void  LimpiarMarcadoresYRutasDeMapa(){

        mMap.clear();

    }

    public void PintarEnElMapa(Double latitudInicial, Double longitudInicial, Double latitudFinal, Double longitudFinal,int r,int v, int a){
        String latitudOrigen=Double.toString(latitudInicial);
        String longitudOrigen=Double.toString(longitudInicial);
        String latitudDestino=Double.toString(latitudFinal);
        String longitudDestino=Double.toString(longitudFinal);
        String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudOrigen+","+longitudOrigen+"&destination="+latitudDestino+","+longitudDestino+"&key=AIzaSyDCYfnub3jihOu0bZw2c3qxRUy-cRqwBrc";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



                try {
                    JSONObject  jso = new JSONObject(response);
                    trazarRuta(jso,r,v,a);
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

    public void ConvertirDireccionEnLatitudYLongitud(String direccion){


        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;

        try {
            address = coder.getFromLocationName(direccion, 1);
            if (address == null) {
                latitud=0;
                longitud=0;

            }
            Address location = address.get(0);
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latitud=lat;
            longitud=lng;


        } catch (Exception e) {

            Toast.makeText(getApplicationContext(),
                    "ERROR", Toast.LENGTH_SHORT);
        }

    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radio de la tierra en  kilómetros
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    public List<String> ObtenerRutasDelPunto(List<String> rutas){

List<String> rutasQuePasanPorElPunto = new ArrayList<String>();


        int contadorA=0;
        int contadorB=0;

        int yaComparo=0;
String[] resultadoIndividual;
String[] separarCoordenadas;


        for(String resultado : rutas){
            contadorA=0;
            contadorB=0;

            resultadoIndividual= resultado.split("<");

for(String analisisIndividual: resultadoIndividual){
    yaComparo=0;
    //Sacar los valores de latitud y longitud actul
    separarCoordenadas= analisisIndividual.split("/");
    latitud= Double.parseDouble(separarCoordenadas[0]);
    longitud= Double.parseDouble(separarCoordenadas[1]);

   // ConvertirDireccionEnLatitudYLongitud(analisisIndividual);


    LatLng PuntoActual = new LatLng(latitud, longitud);
    LatLng PrimerDestino= new LatLng(latitudPuntoEscogido, longitudPuntoEscogido);


    double PrimerDistancia = CalculationByDistance(PuntoActual,PrimerDestino);



    LatLng SegundoDestino= new LatLng(latitudPuntoEscogidoDestino,longitudPuntoEscogidoDestino);

    double SegundaDistancia = CalculationByDistance(PuntoActual,SegundoDestino);

    SegundaDistancia=SegundaDistancia*1000;
    PrimerDistancia=PrimerDistancia*1000;




    //SI la distancia ya sea en el primer punto o en el segundo es menor o igual a 500 metros entonces suma 1

    if(PrimerDistancia <=5000 ){
     yaComparo=1;
     contadorA=1;
    }

    if(yaComparo==0){

        if( SegundaDistancia <=5000){

            contadorB=1;
        }
    }


}

//SI EXISTEN MAS DE DOS PUNTOS POR DONDE PASA ENTONCES AGREGAR A LISTA
if(contadorA==1 && contadorB==1){
    rutasQuePasanPorElPunto.add(resultado);
}


        }



        return rutasQuePasanPorElPunto;
    }

    public String ObtenerDetallesExtrasSobreRuta(String ruta){
        String detalles="";

        InputStream is = getResources().openRawResource(R.raw.data);

        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

        String line = "";

        try {

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");

                if(cols[1].equals(ruta)){

                    detalles=detalles+cols[0]+","+cols[2]+","+cols[3];

                }
            }
        } catch (IOException e) {
            // Prints throwable details
            e.printStackTrace();
        }
   return detalles;

    }
    public  void AgregarEnListaDetalles(String dato){

        listaParaDetalles.add(dato);
    }
    public  void TrazarRutasObtenidasEnElMapa(List<String> rutasObtenidas){
        String[] resultadoIndividual;
        double latitudPuntoActual=0;
        double longitudPuntoActual=0;

        double latitudPuntoSiguiente=0;
        double longitudPuntoSiguiente=0;
        String hex="";

        int contador=0;

        int r=0;
        int v=0;
        int a=0;
        String[] separarCoordenadas;

for(String resultado : rutasObtenidas){
    contador=0;
    resultadoIndividual= resultado.split("<");
    r=new Random().nextInt((255 - 30) + 1) + 30;
    v=new Random().nextInt((255 - 30) + 1) + 30;
    a=new Random().nextInt((255 - 30) + 1) + 30;

    for(String analisisIndividual: resultadoIndividual){
        separarCoordenadas= analisisIndividual.split("/");
        latitud= Double.parseDouble(separarCoordenadas[0]);
        longitud= Double.parseDouble(separarCoordenadas[1]);


        if(contador==0){

            latitudPuntoActual=latitud;
            longitudPuntoActual=longitud;
            contador=contador+1;
        }
        if(contador==1){
            latitudPuntoSiguiente=latitud;
            longitudPuntoSiguiente=longitud;
            PintarEnElMapa(latitudPuntoActual,longitudPuntoActual,latitudPuntoSiguiente,longitudPuntoSiguiente,r,v,a);


            latitudPuntoActual=latitudPuntoSiguiente;
            longitudPuntoActual=longitudPuntoSiguiente;
        }

    }

     hex = String.format("#%02x%02x%02x", r, v, a);
    AgregarEnListaDetalles(ObtenerDetallesExtrasSobreRuta(resultado)+","+hex);



}

    }
}