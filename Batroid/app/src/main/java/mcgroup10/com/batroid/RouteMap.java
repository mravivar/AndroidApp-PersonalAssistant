package mcgroup10.com.batroid;

/**
 * Created by chaitanya on 13/11/16.
 */

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.macisamuele.calendarprovider.EventInfo;
import mcgroup10.com.batroid.Modules.DirectionFinder;
import mcgroup10.com.batroid.Modules.DirectionFinderListener;
import mcgroup10.com.batroid.Modules.Route;

public class RouteMap extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    public static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 0;
    Boolean gpsClicked = false;
    Boolean showedRoute = false;
    private GoogleMap mMap;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Handler mHandler = new Handler();

    public static Date getEnd(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_CALENDAR)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);
            }
        }

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

        GPSEnabled();
    }

    private void sendRequest(String ori, String dest) {
        if (ori.isEmpty()) {
            return;
        }
        if (dest.isEmpty()) {
            return;
        }

        try {
            new DirectionFinder(this, ori, dest).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng tempe = new LatLng(33.421673, -111.936050);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tempe, 18));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    public void GPSEnabled() {
        final int FIVE_SECONDS = 5000;
        mHandler.postDelayed(new Runnable() {
            public void run() {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!statusOfGPS && !gpsClicked) {
                    showDialogGPS();
                } else {
                    getGPSLocation();
                }
                mHandler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);
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
                if (!showedRoute) {
                    String address = gps.getAddress(location).get(0).getAddressLine(0);
                    String postalCode = gps.getAddress(location).get(0).getPostalCode();
                    final String _origin = address; // get from current location
                    Date today = new Date();
                    Date fromDate = new Date();
                    Date toDate = getEnd(today);
                    Log.d("Events from ", fromDate.toString());
                    Log.d("Events to ", toDate.toString());

                    Log.d("Getting all events", "Calendar");
                    String temp_location = null;
                    String event_title = null;
                    String event_description = null;
                    Date fromCalendar = null;
                    int count = 0;
                    for (EventInfo eventInfo : EventInfo.getEvents(this, fromDate, toDate, null, null)) {
                        temp_location = eventInfo.getLocation();
                        fromCalendar = eventInfo.getStartDate();
                        event_title = eventInfo.getTitle();
                        Log.d("events", "location is " + temp_location);
                        Log.d("MainActivity - Events", eventInfo.toString());
                        count++;
                        if (count >= 1) {
                            break;
                        }
                    }
                    final String _destination = temp_location;

                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);

                    if (fromCalendar == null) {
                        Toast.makeText(this, "There are no events in calendar for today!", Toast.LENGTH_LONG).show();
                    }

                    if (fromCalendar != null && fromCalendar.before(toDate) == true) {
                        //Do Action
                        sendRequest(_origin, _destination);

                        final int MY_NOTIFICATION_ID = 1;
                        NotificationManager notificationManager;
                        Notification myNotification;

                        Intent myIntent = new Intent(this, RouteMap.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                this,
                                0,
                                myIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        myNotification = new NotificationCompat.Builder(this)
                                .setContentTitle("New Route Available: From - " + _origin + " to Destination - " + _destination)
                                .setContentText("Leave for " + event_title)
                                .setTicker("Notification!")
                                .setWhen(System.currentTimeMillis())
                                .setContentIntent(pendingIntent)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.start_blue)
                                .build();

                        notificationManager =
                                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
                    }
                    showedRoute = true;
                }
            }
        }
    }

    /**
     * Show a dialog to the user requesting that GPS be enabled
     */
    private void showDialogGPS() {
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