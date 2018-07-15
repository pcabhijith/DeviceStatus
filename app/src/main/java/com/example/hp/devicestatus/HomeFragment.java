package com.example.hp.devicestatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends android.app.Fragment implements LocationListener/*,AdapterView.OnItemSelectedListener*/ {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<String> network_status = new ArrayList<String>();
    ArrayList<String> battery_status = new ArrayList<String>();
    ArrayList<String> time = new ArrayList<String>();


    ArrayList<String> temper = new ArrayList<String>();
    ArrayList<String> details = new ArrayList<String>();


    private TextView batteryLevel_txt, nameOS_txt, networktype_txt, storage_txt;
    int CVersion;
    String deviceName;
    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    Typeface weatherFont;
    TextView locationText;
    LocationManager locationManager;
    String internal_storage, external_storage;

    private Timer myTimer;

    String batteryPer = null;


    int time_interval = 60000;

    private static boolean run = true;

    private boolean isSpinnerTouched = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);

        // Spinner element
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);

        // Spinner click listener
        // spinner.setOnItemSelectedListener(this);

        int initialSelectedPosition = spinner.getSelectedItemPosition();
        spinner.setSelection(initialSelectedPosition, false); //clear selection
        //spinner.setOnItemSelectedListener(this); //set listener after clearing section


        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("1 minuts");
        categories.add("5 minuts");
        categories.add("10 minutes");
        categories.add("15 minutes");
        categories.add("20 minuts");
        categories.add("25 minutes");
        categories.add("1 hour");

        // Creating adapter for spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerTouched = true;
                return false;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                if (!isSpinnerTouched) return;

                String value = parent.getItemAtPosition(position).toString().replaceAll("[^0-9]", "");
                int interval = Integer.parseInt(value);

                if (myTimer != null) {
                    myTimer.cancel();
                    myTimer = null;
                    Toast.makeText(getActivity(), "Scan time changed to " + interval + "minutes", Toast.LENGTH_LONG).show();
                }


                myTimer = new Timer();
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        getActivity().runOnUiThread(Timer_Tick);

                    }

                }, 0, interval * 60000);


                //Toast.makeText(getActivity(),value,Toast.LENGTH_LONG).show();

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);


        batteryLevel_txt = (TextView) v.findViewById(R.id.battery_level);
        nameOS_txt = (TextView) v.findViewById(R.id.nameOs_view);
        networktype_txt = (TextView) v.findViewById(R.id.network_view);
        storage_txt = (TextView) v.findViewById(R.id.storage_view);

        detailsField = (TextView) v.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) v.findViewById(R.id.current_temperature_field);
        humidity_field = (TextView) v.findViewById(R.id.humidity_field);
        pressure_field = (TextView) v.findViewById(R.id.pressure_field);
        weatherIcon = (TextView) v.findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weathericons-regular-webfont.ttf");


        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                getActivity().runOnUiThread(Timer_Tick);


            }

        }, 0, time_interval);


        getActivity().registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));//Battery information
        deviceInfo(); //Device name and os
        networkType(); //Network type
        storage();//device storage
        getLocation();

        // temper.add("hsdjhjdf");
        return v;
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            batteryPer = "Battery Status: " + String.valueOf(level) + "%";
            batteryLevel_txt.setText(batteryPer);
            battery_status.add(batteryPer);

        }
    };

    @Override
    public void onLocationChanged(Location location) {
        Weather.placeIdTask asyncTask = new Weather.placeIdTask(new Weather.AsyncResponse() {


            public void processFinish(/*String weather_city, */ final String weather_description, final String weather_temperature,
                                                                String weather_humidity, String weather_pressure, String weather_updatedOn,
                                                                String weather_iconText, String sun_rise) {


                // cityField.setText(weather_city);
                // updatedField.setText(weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidity: " + weather_humidity);
                pressure_field.setText("Pressure: " + weather_pressure);
                weatherIcon.setText(Html.fromHtml(weather_iconText));
                weatherIcon.setTypeface(weatherFont);


                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //getActivity().runOnUiThread(Timer_Tick);
                        temper.add(weather_temperature);


                        details.add(weather_description);


                    }

                }, 0, time_interval);


            }


        });//asyncTask.execute("8.5241", "76.9366   "); //  asyncTask.execute("Latitude", "Longitude")
        asyncTask.execute("" + location.getLatitude(), "" + location.getLongitude());


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }


    public static String formatSize(long size) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }


    public void deviceInfo() {
        CVersion = android.os.Build.VERSION.SDK_INT;
        deviceName = android.os.Build.MANUFACTURER;
        switch (CVersion) {
            case 11:
                nameOS_txt.setText("Honeycomb" + "  " + deviceName);
                break;
            case 12:
                nameOS_txt.setText("Honeycomb" + "  " + deviceName);
                break;
            case 13:
                nameOS_txt.setText("Honeycomb" + "  " + deviceName);
                break;
            case 14:
                nameOS_txt.setText("Ice Cream Sandwich" + "  " + deviceName);
                break;
            case 15:
                nameOS_txt.setText("Ice Cream Sandwich" + "  " + deviceName);
                break;
            case 16:
                nameOS_txt.setText("Jelly Bean" + "  " + deviceName);
                break;
            case 17:
                nameOS_txt.setText("Jelly Bean" + "  " + deviceName);
                break;
            case 18:
                nameOS_txt.setText("Jelly Bean" + "  " + deviceName);
                break;
            case 19:
                nameOS_txt.setText("KitKat" + "  " + deviceName);
                break;
            case 21:
                nameOS_txt.setText("Lollipop" + "  " + deviceName);
                break;
            case 22:
                nameOS_txt.setText("Lollipop" + "  " + deviceName);
                break;
            case 23:
                nameOS_txt.setText("Marshmallow" + "  " + deviceName);
                break;
            case 24:
                nameOS_txt.setText("Nougat" + "  " + deviceName);
                break;
            case 25:
                nameOS_txt.setText("Nougat" + "  " + deviceName);
                break;
            default:
                Toast.makeText(getActivity(), "Not Found", Toast.LENGTH_LONG).show();
                break;

        }
    }


    public void getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 2, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void storage() {
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Boolean isSDSupportedDevice = Environment.isExternalStorageRemovable();
        if (isSDSupportedDevice && isSDPresent) {
            Toast.makeText(getActivity(), "Yes SD card available", Toast.LENGTH_LONG).show();
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long bytesAvailable;
            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
            } else {
                bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
            }
            long megAvailable = bytesAvailable / (1024 * 1024);
            Log.e("", "Available MB : " + megAvailable);

            external_storage = formatSize(megAvailable);
            storage_txt.setText("External is :" + external_storage + "Internal is :" + internal_storage);


        } else {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            internal_storage = formatSize(totalBlocks * blockSize);

            storage_txt.setText("No SD card ," + "  Internal is :" + internal_storage);

        }
    }

    public void networkType() {
        ConnectivityManager connectivitymanager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivitymanager.getAllNetworkInfo();
        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    networktype_txt.setText("Connected to WiFi");

            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected()) {
                    networktype_txt.setText("Connected to Mobile Data");
                }
        }
    }


    private Runnable Timer_Tick = new Runnable() {
        public void run() {


            mAdapter = new MyAdapter(network_status, battery_status, time, temper, details);
            mRecyclerView.setAdapter(mAdapter);


            //network status
            ConnectivityManager connectivitymanager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] networkInfo = connectivitymanager.getAllNetworkInfo();
            for (NetworkInfo netInfo : networkInfo) {
                if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                    if (netInfo.isConnected())
                        //networktype_txt.setText("Connected to WiFi");
                        network_status.add("Connected to WiFi");

                if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (netInfo.isConnected()) {
                        //networktype_txt.setText("Connected to Mobile Data");

                        network_status.add("Connected to Mobile Data");


                    }
            }
            //Time
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
            String strDate = "Time : " + mdformat.format(calendar.getTime());
            time.add(strDate);


            // battery_status
            getActivity().registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));//Battery information


        }
    };


  /*  @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        String number= item.replaceAll("[^0-9]", "");
        time_interval= (Integer.parseInt(number))*60000;

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

      *//*  if (!isSpinnerTouched){
            String item = parent.getItemAtPosition(position).toString();
            String number= item.replaceAll("[^0-9]", "");
            time_interval= (Integer.parseInt(number))*60000;

        }*//*


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {


    }*/
}