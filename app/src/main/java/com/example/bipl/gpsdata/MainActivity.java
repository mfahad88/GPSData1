package com.example.bipl.gpsdata;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView tv;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        tv = (TextView) findViewById(R.id.textView);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Button pressed..", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                Thread.sleep(1000);
                                Log.e("Thread----->", String.valueOf(Thread.currentThread().getId()));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            tv.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            int hrs = Integer.parseInt(DateFormat.format("hh", new Date(location.getTime())).toString());
                                            int mins = Integer.parseInt(DateFormat.format("mm", new Date(location.getTime())).toString());
                                            int secs = Integer.parseInt(DateFormat.format("ss", new Date(location.getTime())).toString());
                                            String med=DateFormat.format("aa", new Date(location.getTime())).toString();
                                            String date=DateFormat.format("dd MMM, yyyy", new Date(location.getTime())).toString();
                                            tv.setText("Latitude: " + location.getLatitude() + "\n" + "Longitude: " + location.getLongitude()+"\n"+
                                            "Accuracy: "+location.getAccuracy()+"\n"+"Speed: "+location.getSpeed()+"\n"+"Date: "+date+"\n"+"Time: "+hrs + " : " + mins + " : " + secs+" "+med);
                                        }

                                        @Override
                                        public void onStatusChanged(String provider, int status, Bundle extras) {

                                        }

                                        @Override
                                        public void onProviderEnabled(String provider) {

                                        }

                                        @Override
                                        public void onProviderDisabled(String provider) {

                                        }
                                    });
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }


}
