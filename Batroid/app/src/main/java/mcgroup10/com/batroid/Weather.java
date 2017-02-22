package mcgroup10.com.batroid;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mayankkhullar on 13/11/16.
 */

public class Weather extends AppCompatActivity {

    //check if enable GPS popup is active
    Boolean gpsClicked = false;
    private Handler mHandler = new Handler();

    public Weather() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }

        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        1);
            }
        }
        GPSEnabled();
    }

    public void RefreshLoc(View v) {
        GPSEnabled();
    }

    public void GPSEnabled() {
        final int One_Min = 5000 * 12;
        final GpsService gps = new GpsService(this);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!statusOfGPS && !gpsClicked) {
                    showDialogGPS();
                } else {
                    getGPSLocation();
                }
                mHandler.postDelayed(this, One_Min);
            }
        }, One_Min);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS && !gpsClicked) {
            showDialogGPS();
        } else {
            getGPSLocation();
        }
    }


    public void getGPSLocation() {
        GpsService gps = new GpsService(this);
        if (gps.canGetLocation) {
            Intent intent = new Intent(this, GpsService.class);
            startService(intent);
            Location location = gps.getLocation();

            if (location != null) {
                String address = gps.getAddress(location).get(0).getAddressLine(0);
                String postalCode = gps.getAddress(location).get(0).getPostalCode();
                String city = gps.getAddress(location).get(0).getLocality();
                TextView ed = (TextView) findViewById(R.id.Add);
                ed.setText(address + ", " + city);

                TextView cond = (TextView) findViewById(R.id.cond);
                TextView temp = (TextView) findViewById(R.id.temp);
                ImageView condIcon = (ImageView) findViewById(R.id.condIcon);
                new WeatherUpdates(this, temp, condIcon, cond).execute(postalCode);
            }
        }
    }

    /**
     * Show a dialog to the user requesting that GPS be enabled
     */
    public void showDialogGPS() {
        gpsClicked = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Enable GPS");
        builder.setMessage("Please enable GPS");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.dismiss();
                gpsClicked = false;
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                gpsClicked = false;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
