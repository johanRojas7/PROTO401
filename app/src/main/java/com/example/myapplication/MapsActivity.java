package com.example.myapplication;


import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
import com.google.android.gms.maps.model.Polyline;
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

import java.util.Arrays;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private String TAG = "so47492459";
    private ToggleButton cambiarRuta;
    public double latitud;
    public double longitud;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        cambiarRuta= findViewById(R.id.botonGenerarRuta);


        Places.initialize(getApplicationContext(), "AIzaSyDCYfnub3jihOu0bZw2c3qxRUy-cRqwBrc");

        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


        ////FRAGMENTE 2

        AutocompleteSupportFragment autocompleteFragment2 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment2);

        // Specify the types of place data to return.
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap=googleMap;

        LatLng localizacionInicial = new LatLng(10.273563, -84.0739102);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacionInicial,8));
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e( "MapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }


    }

    public  void CuandoSePresionaTiempoReal(View view){
        LimpiarMarcadoresYRutasDeMapa();

        String latitudOrigen="11.041500547069788";
        String longitudOrigen="-85.62270467144855";
        String latitudDestino="10.621058940883586";
        String longitudDestino="-85.44338934257117";
        String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudOrigen+","+longitudOrigen+"&destination="+latitudDestino+","+longitudDestino+"&key=AIzaSyDCYfnub3jihOu0bZw2c3qxRUy-cRqwBrc";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



                try {
                    JSONObject  jso = new JSONObject(response);
                    trazarRuta(jso);
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


    private void trazarRuta(JSONObject jso) {

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
                        mMap.addPolyline(new PolylineOptions().addAll(list).color(Color.RED).width(8));



                    }



                }



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void CuandoSePresionaGenerarRuta(View view){


        GenerarMarcadoresEnElMapa("Primer Marcador",10.273563,-84.0739102);
        GenerarMarcadoresEnElMapa("Segundo marcador",10.279915739245865,-84.44259212625448);

        TrazarRutasDeMarcadoresActualesEnElMapa(10.273563,-84.0739102,10.279915739245865,-84.44259212625448);

        if(view.getId()==R.id.botonGenerarRuta){
            if(cambiarRuta.isChecked()){
            ;


            }else{

            }


        }

    }

    public void GenerarMarcadoresEnElMapa(String titulo,double latitud, double longitud){


        LatLng marcador = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(marcador).title(titulo));

    }

    public void  LimpiarMarcadoresYRutasDeMapa(){

        mMap.clear();

    }

    public void TrazarRutasDeMarcadoresActualesEnElMapa(Double latitudInicial, Double longitudInicial, Double latitudFinal, Double longitudFinal){


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



}