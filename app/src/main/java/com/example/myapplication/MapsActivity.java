package com.example.myapplication;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    public GoogleMap mMap;

    public double latitud;
    public double longitud;
    public double latitudPuntoEscogido=0;
    public double longitudPuntoEscogido=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




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

    @Override
    public void onMapClick(LatLng punto){


latitudPuntoEscogido=punto.latitude;
longitudPuntoEscogido=punto.longitude;
        LimpiarMarcadoresYRutasDeMapa();
        GenerarMarcadoresEnElMapa("Rutas Obtenidas",punto.latitude,punto.longitude);

        List<String> objetoRuta = new ArrayList<String>();


        objetoRuta.add("11.210469352249802,-85.61205451379017<11.0730511145966,-85.6319528279177<10.628473892401459,-85.44223100553933");
        objetoRuta.add("10.628473892401459,-85.44223100553933<10.301242932408854,-85.83901801528522");
        objetoRuta.add("11.0730511145966,-85.6319528279177<11.061122708401172,-85.41553022892087");


        TrazarRutasObtenidasEnElMapa(ObtenerRutasDelPunto(objetoRuta));

       // Toast.makeText(getApplicationContext(),"Hola",Toast.LENGTH_LONG).show();
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
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapa));

            if (!success) {
                Log.e( "MapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }


    }

    public  void CuandoSePresionaDetalles(View view){

        //Desde  maps activiy hasta detalles
        Intent siguiente = new Intent(MapsActivity.this,DetallesActivity.class);
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


        int contadorDePuntos=0;

String[] resultadoIndividual;
String[] separarCoordenadas;


        for(String resultado : rutas){
            resultadoIndividual= resultado.split("<");

for(String analisisIndividual: resultadoIndividual){
    //Sacar los valores de latitud y longitud actul
    separarCoordenadas= analisisIndividual.split(",");
    latitud= Double.parseDouble(separarCoordenadas[0]);
    longitud= Double.parseDouble(separarCoordenadas[1]);

   // ConvertirDireccionEnLatitudYLongitud(analisisIndividual);


    LatLng PuntoActual = new LatLng(latitud, longitud);
    LatLng PrimerDestino= new LatLng(latitudPuntoEscogido, longitudPuntoEscogido);


    double PrimerDistancia = CalculationByDistance(PuntoActual,PrimerDestino);


    PrimerDistancia=PrimerDistancia*1000;



    //SI la distancia ya sea en el primer punto o en el segundo es menor o igual a 500 metros entonces suma 1

    if(PrimerDistancia <=5000 ){
     rutasQuePasanPorElPunto.add(resultado);
     contadorDePuntos=contadorDePuntos+1;
    }

}


        }



        return rutasQuePasanPorElPunto;
    }

    public  void TrazarRutasObtenidasEnElMapa(List<String> rutasObtenidas){
        String[] resultadoIndividual;
        double latitudPuntoActual=0;
        double longitudPuntoActual=0;

        double latitudPuntoSiguiente=0;
        double longitudPuntoSiguiente=0;


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
        separarCoordenadas= analisisIndividual.split(",");
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


}

    }
}