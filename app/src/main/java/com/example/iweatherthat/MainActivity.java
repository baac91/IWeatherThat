package com.example.iweatherthat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
//import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.iweatherthat.model.DailyWeatherReport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationServices;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
//    final String URL_BASE = "https://api.climacell.co/v3/weather/realtime";
    final String URL_COORD = "?lat=";
//    final String URL_COORD = "?lat=40.5520491&lon=-74.2587568";

//    final String URL_OPTIONS = "&unit_system=us&fields=wind_gust%2Ctemp%2Cfeels_like%2Cprecipitation%2Csunrise%2Csunset";
//    final String URL_API_KEY = "&apikey=7ENTUzvN3evzjGz1Vf7IIPURvZk1BJI9";

    final String URL_BASE = "https://api.openweathermap.org/data/2.5/forecast/daily";
    final String URL_OPTIONS = "&units=imperial";
//    final String URL_API_KEY = "&appid=8442306bc6d8ffad931e6055702363d0";
final String URL_API_KEY = "&appid=0936895a398e594b0531599fe4c896cd";




    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSION_LOCATION = 111;
    private ArrayList<DailyWeatherReport> weatherReportList = new ArrayList<>();

    private ImageView weatherIconMini;
    private TextView weatherDate;
    private TextView currentTemp;
    private TextView lowtemp;
    private TextView cityCountry;
    private TextView weatherDescription;
    private ImageView weatherIcon;

    WeatherAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherIcon = (ImageView)findViewById(R.id.weatherIcon);
        weatherIconMini = (ImageView)findViewById(R.id.weatherIconMini);
        weatherDate = (TextView)findViewById(R.id.weatherDate);
        currentTemp = (TextView)findViewById(R.id.currentTemp);
        lowtemp = (TextView)findViewById(R.id.lowerTemp);
        cityCountry = (TextView)findViewById(R.id.cityCountry);
        weatherDescription = (TextView)findViewById(R.id.weatherDescription);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.content_weather_reports);

        mAdapter = new WeatherAdapter(weatherReportList);

        recyclerView.setAdapter(mAdapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(layoutManager);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this,this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
    public void downloadWeatherData(Location location ){
        final String fullCoords = URL_COORD + location.getLatitude() + "&lon=" + location.getLongitude();
        final String url = URL_BASE + fullCoords + URL_OPTIONS +  URL_API_KEY;
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Fun", "RES: " + response.toString());

                try {
                    JSONObject city = response.getJSONObject("city");
                    String cityName = city.getString("name");
                    String country = city.getString("country");
                    JSONArray list = response.getJSONArray("list");
//                    Log.v("JSON", "Name: " + cityName + " - " + "country: " + country);
//                    JSONObject obj_b = list.getJSONObject(2);
//                    JSONObject main_a = obj_b.getJSONObject("main");
//                    Log.v("JSON", "Name: " + main_a);

                        for(int i = 0; i < 15; i++) {
                            JSONObject obj = list.getJSONObject(i);
//                            JSONObject main = obj.getJSONObject("main");
//                            Double currentTemp = main.getDouble("temp");
//                            Double maxTemp = main.getDouble("temp_max");
//                            Double minTemp = main.getDouble("temp_min");

                            JSONObject temp = obj.getJSONObject("temp");

                            Double currentTemp = temp.getDouble("day");
                            Double maxTemp = temp.getDouble("max");
                            Double minTemp = temp.getDouble("min");


                            JSONArray weatherArr = obj.getJSONArray("weather");
                            JSONObject weather = weatherArr.getJSONObject(0);
                            String weatherType = weather.getString("main");
//
//                            String rawDate = obj.getString("dt_txt");
                            Double rawDate = obj.getDouble("dt");

                            DailyWeatherReport report = new DailyWeatherReport(cityName, currentTemp.intValue(),maxTemp.intValue(),minTemp.intValue(),country, weatherType, rawDate.intValue());

                            Log.v("Sucker!", "Printing from class: " + report.getWeather());
                            weatherReportList.add(report);
                        }
                } catch (JSONException e){
                    Log.v("JSON", "EXC" + e.getLocalizedMessage());
                }
                    updateUI();
                    mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Fun", "Err:" + error.getLocalizedMessage());
            }

        });
        Volley.newRequestQueue(this).add(jsonRequest);
    }

    public void updateUI(){
        if(weatherReportList.size() > 0){
            DailyWeatherReport report = weatherReportList.get(0);

            switch(report.getWeather()){
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.mipmap.cloudy));
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.mipmap.cloudy));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.mipmap.rainy));
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.mipmap.rainy));
                    break;
                default:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.mipmap.sunny));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.mipmap.sunny));
            }

//            weatherDate.setText("TODAY, May 1");
            weatherDate.setText(report.getFormattedDate());
            currentTemp.setText(Integer.toString(report.getCurrentTemp()));
            lowtemp.setText(Integer.toString(report.getMinTemp()));
            cityCountry.setText(report.getCityName() + ", " + report.getCountry());
            weatherDescription.setText(report.getWeather());

        }




    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        downloadWeatherData(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
        } else {
            startLocationServices();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    public void startLocationServices() {
        try {
            LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,req,this);
        } catch (SecurityException exception) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationServices();
                } else {
                    //show a dialog saying something like, "I can't run your location dummy - you denied permission!"
                    Toast.makeText(this, "Do something about it", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public class WeatherAdapter extends RecyclerView.Adapter<WeatherReportViewHolder>{
        private  ArrayList<DailyWeatherReport> mDailyWeatherReports;

        public WeatherAdapter(ArrayList<DailyWeatherReport> dailyWeatherReports){
            mDailyWeatherReports = dailyWeatherReports;
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherReportViewHolder holder, int position) {
                DailyWeatherReport report = mDailyWeatherReports.get(position);
                holder.updateUI(report);
        }

        @Override
        public int getItemCount() {

            return mDailyWeatherReports.size();
        }

        @NonNull
        @Override
        public WeatherReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather, parent, false);
            return new WeatherReportViewHolder(card);
        }
    }


    public class WeatherReportViewHolder extends RecyclerView.ViewHolder{

        private  ImageView list_weatherIcon;
        private  TextView list_weatherDate;
        private  TextView list_weatherDescription;
        private  TextView list_tempHigh;
        private  TextView list_tempLow;

        public WeatherReportViewHolder(@NonNull View itemView) {
            super(itemView);
            list_weatherIcon = (ImageView)itemView.findViewById(R.id.list_weather_icon);
            list_weatherDate = (TextView)itemView.findViewById(R.id.list_weather_day);
            list_weatherDescription = (TextView)itemView.findViewById(R.id.list_weather_description);
            list_tempHigh = (TextView)itemView.findViewById(R.id.list_weather_temp_high);
            list_tempLow  = (TextView)itemView.findViewById(R.id.list_weather_temp_low);
        }

        public void updateUI(DailyWeatherReport report){
            list_weatherDate.setText(report.getFormattedDate());
            list_weatherDescription.setText(report.getWeather());
            list_tempHigh.setText(Integer.toString(report.getMaxTemp()));
            list_tempLow.setText(Integer.toString(report.getMinTemp()));

            switch(report.getWeather()){
                case DailyWeatherReport.WEATHER_TYPE_CLEAR:
                    list_weatherIcon.setImageDrawable(getResources().getDrawable(R.mipmap.sunny_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    list_weatherIcon.setImageDrawable(getResources().getDrawable(R.mipmap.cloudy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    list_weatherIcon.setImageDrawable(getResources().getDrawable(R.mipmap.rainy_mini));
                    break;
                default:
                    list_weatherIcon.setImageDrawable(getResources().getDrawable(R.mipmap.sunny_mini));
            }

        }

    }
}