package com.example.bipl.gpsdata;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView tv,tv1;
    LocationManager locationManager;
    String myLoc = null;
    float myLat, myLon;
    Boolean status = false;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        tv = (TextView) findViewById(R.id.textView);
        tv1 = (TextView) findViewById(R.id.textView1);
        tv.setVisibility(View.INVISIBLE);
        tv1.setVisibility(View.INVISIBLE);
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Fetching Weather...");
        progressDialog.setMessage("Loading...");
/*        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        sendBroadcast(intent);*/
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

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
                    public void onLocationChanged(final Location location) {
                        final int hrs = Integer.parseInt(DateFormat.format("hh", new Date(location.getTime())).toString());
                        final int mins = Integer.parseInt(DateFormat.format("mm", new Date(location.getTime())).toString());
                        final int secs = Integer.parseInt(DateFormat.format("ss", new Date(location.getTime())).toString());
                        final String med = DateFormat.format("aa", new Date(location.getTime())).toString();
                        final String date = DateFormat.format("dd MMM, yyyy", new Date(location.getTime())).toString();
                        if ((location.getLatitude()) != 0.0 && (location.getLongitude()) != 0.0) {
                            myLat = (float) location.getLatitude();
                            myLon = (float) location.getLongitude();
                        }
                        tv.setText("Latitude: " + location.getLatitude() + "\n" + "Longitude: " + location.getLongitude() + "\n" +
                                "Accuracy: " + location.getAccuracy() + "\n" + "Speed: " + location.getSpeed() + "\n" + "Date: " + date + "\n" + "Time: " + hrs + " : " + mins + " : " + secs + " " + med);


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


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!status) {
                            if (myLat != 0.0 && myLat != 0.0) {
                                new locationAsync().execute(myLat+","+myLon);
                                status = true;
                            }
                        }
                    }
                }).start();

            }
        });
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    class locationAsync extends AsyncTask<String,List,List>{


        @Override
        protected List doInBackground(String... params) {
            List list=new ArrayList();
            try {
                String queryString = "http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query=" + params[0];

                URL url = new URL(queryString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-length", "0");
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);
                conn.setConnectTimeout(100000);
                conn.setReadTimeout(100000);
                Log.e("Connection------->", conn.toString());
                InputStream stream=conn.getInputStream();

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(stream);
                Element element=doc.getDocumentElement();
                element.normalize();
                NodeList nList = doc.getElementsByTagName("display_location");
                NodeList nList1 = doc.getElementsByTagName("current_observation");
                Node node_loc=nList.item(0);

                Node node_temp=nList1.item(0);
                Element element_loc=(Element)node_loc;
                Element element_temp=(Element)node_temp;
                Element element_humd=(Element)node_temp;
                Element element_wind=(Element)node_temp;
                Element element_press=(Element)node_temp;
                list.add(getValue("full",element_loc));
                list.add(getValue("temperature_string",element_temp));
                list.add(getValue("relative_humidity",element_humd));
                list.add(getValue("wind_string",element_wind));
                list.add(getValue("pressure_string",element_press));
                Log.e("Node1>>>>", getValue("temperature_string",element_temp));
                Log.e("Node>>>>>>", getValue("full",element_loc));

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(final List list) {
            super.onPostExecute(list);
            if(list.size()>0){
                progressDialog.dismiss();
            }

            tv1.post(new Runnable() {
                @Override
                public void run() {
                tv1.setText("\nLocation: "+String.valueOf(list.get(0))+"\nTemperature: "+String.valueOf(list.get(1))+"\nHumidity: "+list.get(2)
                +"\nWind: "+String.valueOf(list.get(3))+"\nPressure: "+String.valueOf(list.get(4)));
                    tv.setVisibility(View.VISIBLE);
                    tv1.setVisibility(View.VISIBLE);
                }
            });
        }
    }

}