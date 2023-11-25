package android.example.weatherforcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTv,Temperaturetv,ConditionTv;
    private RecyclerView weatherRv;
    private TextInputEditText cityEdit;
    private ImageView backIV,IconIV,searchIV;
    private ArrayList<WeatherRvModel> weatherRvModelArrayList;
    private WeatherRVAdapter weatherRVAdapater;
    private LocationManager locationManager;
    private int PERMISSION_CODE=1; // gradding the permission
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeRL=findViewById(R.id.idRLHome);
        loadingPB=findViewById(R.id.idPBLoading);
        cityNameTv=findViewById(R.id.idTVCityName);
        Temperaturetv=findViewById(R.id.idTVTemperature);
        ConditionTv=findViewById(R.id.idTVCondition);
        weatherRv=findViewById(R.id.idRvWeather);
        cityEdit=findViewById(R.id.idEditCity);
        backIV=findViewById(R.id.idIVBack);
        IconIV=findViewById(R.id.idICIcon);
        searchIV=findViewById(R.id.idIVSearch);
        weatherRvModelArrayList= new ArrayList<>();
      weatherRVAdapater=new WeatherRVAdapter(this ,weatherRvModelArrayList);
        weatherRv.setAdapter(weatherRVAdapater);



      locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }
        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
       // cityName=getCityName(location.getLongitude(),location.getLatitude());//
      // getWeatherInfo(cityName);
        if(location!=null){

            cityName=getCityName(location.getLongitude() ,location.getLatitude());
            getWeatherInfo(cityName);

        }else{
            Toast.makeText(this, "Invalid user location", Toast.LENGTH_SHORT).show();


        }


       searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String City=cityEdit.getText().toString();// check city name empty or not
                if(City.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();

                }else{
                    cityNameTv.setText(cityName);
                    getWeatherInfo(City);
                }
            }
        });




    }
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude , double latitude){
        String cityName="Not Found";
        Geocoder gcd=new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses= gcd.getFromLocation(latitude,longitude,10);
            for(Address adr: addresses){
                if(adr!=null){
                    String city=adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName=city;
                    }else{
                        Log.d("TAG","City is not found");
                        Toast.makeText(this, "User City Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }


    private void getWeatherInfo(String cityName){
        String url="http://api.weatherapi.com/v1/forecast.json?key=42fa37ea89a74d3cbc5104331230502&q="+ cityName +"&days=1&aqi=yes&alerts=yes";
        Log.d("inside--we--1",cityName);
        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRvModelArrayList.clear();// user multiple time for wetaher
                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    Temperaturetv.setText(temperature + "°c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(IconIV);
                    ConditionTv.setText(condition);
                    if (isDay == 1) {
                        //Morning
                        Picasso.get().load("https://images.pexels.com/photos/2093252/pexels-photo-2093252.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1").into(backIV); // passing the background
                    } else {
                        Picasso.get().load("https://images.pexels.com/photos/1257860/pexels-photo-1257860.jpeg?auto=compress&cs=tinysrgb&w=600").into(backIV); // passing the images
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArry = forecastO.getJSONArray("hour");

                    for (int i = 0; i < hourArry.length(); i++) {
                        JSONObject hourObj = hourArry.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRvModelArrayList.add(new WeatherRvModel(time, temper, img, wind));


                    }
                    weatherRVAdapater.notifyDataSetChanged(); // notify the data will be chnaged in arrylist
                    Log.d("inside--we--2",cityName);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Enter Valid City Name", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }





}

