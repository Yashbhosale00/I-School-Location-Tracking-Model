package com.example.locationdetect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;

public class SalesExecutive_Activity extends AppCompatActivity {
    Button logout;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int FOREGROUND_SERVICE_PERMISSION_REQUEST_CODE = 1003;
    private static final long LOCATION_UPDATE_INTERVAL = 1000;
    private Switch aSwitch;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Timer locationUpdateTimer;
    private static final String SWITCH_STATE_KEY = "switch_state";

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_executive);

        aSwitch = findViewById(R.id.switch1);
        logout = findViewById(R.id.SalesLogout);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    startLocationUpdateService();
                    requestLocationPermission();
                    startLocationUpdates();
                    requestForegroundServicePermission();
                    Toast.makeText(SalesExecutive_Activity.this, "Please Enable Your Phone Location", Toast.LENGTH_SHORT).show();
                } else {
                    stopLocationUpdates();
                }
                saveSwitchState(isChecked);
            }
        });

        boolean savedSwitchState = getSwitchState();
        aSwitch.setChecked(savedSwitchState);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogout();
            }
        });
    }

    private void handleLogout() {
        stopLocationUpdates();

        saveSwitchState(false);

        Intent intent = new Intent(SalesExecutive_Activity.this, LoginActivity.class);
        startActivity(intent);

        finish();
        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
    }

    private void saveSwitchState(boolean isChecked) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SWITCH_STATE_KEY, isChecked);
        editor.apply();
    }
    private void startLocationUpdateService() {
        Intent serviceIntent = new Intent(this, LocationUpdateService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void requestForegroundServicePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, FOREGROUND_SERVICE_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private boolean getSwitchState() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean(SWITCH_STATE_KEY, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    Log.e("Permission", "Location permission denied");
                }
                break;

            case FOREGROUND_SERVICE_PERMISSION_REQUEST_CODE:
                break;
        }
    }

    private void startLocationUpdates() {
        if (!isServiceRunning(LocationUpdateService.class)) {
            Intent serviceIntent = new Intent(this, LocationUpdateService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            }

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        Location location = locationResult.getLastLocation();
                        // Handle location updates
                        Log.d("LocationUpdate", "Location received: " + location.getLatitude() + ", " + location.getLongitude());
                    }
                }
            };

            locationUpdateTimer = new Timer();
            locationUpdateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (checkLocationPermission()) {
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                    } else {
                        Log.e("Permission", "Location permission not granted or switch is off");
                    }
                }
            }, 0, LOCATION_UPDATE_INTERVAL);
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (locationUpdateTimer != null) {
            locationUpdateTimer.cancel();
        }
        Intent serviceIntent = new Intent(this, LocationUpdateService.class);
        stopService(serviceIntent);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}