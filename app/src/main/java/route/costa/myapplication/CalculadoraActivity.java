package route.costa.myapplication;

import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.costa.myapplication.MapsActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

import java.util.Date;
import java.util.List;

public class CalculadoraActivity extends AppCompatActivity {
    public double latitud;
    public double longitud;
    private TimePicker timePicker1;

    public  String API_KEY = "AIzaSyDCYfnub3jihOu0bZw2c3qxRUy-cRqwBrc";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculadora);


        Places.initialize(getApplicationContext(), "AIzaSyDCYfnub3jihOu0bZw2c3qxRUy-cRqwBrc");

        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        autocompleteFragment.setCountry("CR");
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS));


        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                AgregarCoordenadasObtenidasEnAutocomplete(place.getAddress());

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CuandoSePresionaObtener(View vista){
        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
        int hour = timePicker1.getCurrentHour();
        int min = timePicker1.getCurrentMinute();

/*

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Prediccion tiempo de llegada");
        builder.setMessage("Tu autobus pasa alrededor de las ");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Hacer cosas aqui al hacer clic en el boton de aceptar
            }
        });
        builder.show();
*/
String a= "Boston,MA|Charlestown,MA";
String b= "Lexington,MA|Concord,MA";


aa();
     int kk=0;
     int jj=0;



    }
    public void AgregarCoordenadasObtenidasEnAutocomplete(String direccion){
        ConvertirDireccionEnLatitudYLongitud(direccion);

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

    public void aa(){

        String url ="https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC|Seattle&destinations=San+Francisco|Victoria+BC&mode=bicycling&language=fr-FR&key=AIzaSyDCYfnub3jihOu0bZw2c3qxRUy-cRqwBrc";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "String response=" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "String error=" + error);

            }
        });
        queue.add(stringRequest);


    }
    private void trazarRuta(JSONObject jso) {

        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
JSONObject jj=jso;
int x=0;
int b=0;
int jjk=0;

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




                    }


                }



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
