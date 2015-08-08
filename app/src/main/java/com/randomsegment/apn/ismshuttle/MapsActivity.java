package com.randomsegment.apn.ismshuttle;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;


import java.util.*;

public class MapsActivity extends ActionBarActivity {
    private GoogleMap mMap;
    private boolean moreThanOne = false;
    public LatLng prev = new LatLng(0, 0);
    public int DEFAULT_ZOOM_LEVEL = 18;
    private Marker marker;
    Context context;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main_appbar);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationDrawerFragment navigationDrawerFragment =
                (NavigationDrawerFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_navigation_drawer);

        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        createKML(context);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                onMapReady();
            }
        }
    }

    /**
     * To Add a KML Layer to Map
     * createKML() and findMap()
     */

    private void createKML(Context context) {
        try {
            KmlLayer routeLayer = new KmlLayer(findMap(), R.raw.route1, context);
            routeLayer.addLayerToMap();
        }
        catch (Exception e) {
            // Something
        }
    }

    private GoogleMap findMap() {
        return ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */


    private double coordinates(double f) {
        int a = (int) f;
        double frac = (f - (double) a) / 0.6;
        return (double) a + frac;
    }
    private class getMapData extends AsyncTask<Void, Void, String>{
        private HttpParams httpParams;
        private HttpClient httpclient;
        private HttpPost httppost;
        private int TIMEOUT_MILLISEC = 10000;
        private String url;
        private Context m;
        private ResponseHandler<String> responseHandler;
        public getMapData(Context c){
            m = c;
        }
        @Override
        protected void onPreExecute(){
            httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            httpclient = new DefaultHttpClient(httpParams);
            url = new String("http://shuttletracker.hostei.com/?bus=1&latest=1");
            //Toast.makeText(m, "abc", Toast.LENGTH_LONG).show();
        }
        @Override
        protected String doInBackground(Void... params){
            httppost = new HttpPost(url);
            responseHandler = new BasicResponseHandler();
            String lat = new String();
            String lng = new String();
            try {
                String responseBody = httpclient.execute(httppost, responseHandler);
                JSONObject json = new JSONObject(responseBody);
                lat = json.getString("latitude");
                lng = json.getString("longitude");
                //Log.d("data",lat + " " + lng);
            }catch (ClientProtocolException e){
                e.printStackTrace();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return lat+" "+lng;
        }
        //@Override
        protected void onPostExecute(String result){
            Calendar c = Calendar.getInstance();
            int seconds = c.get(Calendar.SECOND);
            if (result.length() >= 8) {
                String[] coords = result.split(" ");
                if (coords[0].length() > 0 && coords[1].length() > 0) {
                    Double lat = Double.parseDouble(coords[0]);
                    Double lng = Double.parseDouble(coords[1]);
                    LatLng myLoc = new LatLng(coordinates(lat / 100.0), coordinates(lng / 100.0));
                    float curZoom = mMap.getCameraPosition().zoom;
                    //mMap.clear();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, Math.max(DEFAULT_ZOOM_LEVEL, curZoom)));
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    if (!moreThanOne){
                        moreThanOne = true;
                        prev = myLoc;
                        marker = mMap.addMarker(new MarkerOptions()
                                .title("ISM")
                                .snippet("Indian School Of Mines.")
                                .position(myLoc));
                        marker.setVisible(true);
                        //Toast.makeText(m, "more than one done " + Double.toString(prev.latitude), Toast.LENGTH_LONG).show();
                    }
                    else{
                        marker.setPosition(myLoc);
                        //Toast.makeText(m, Double.toString(myLoc.latitude) +" "+ Double.toString(myLoc.longitude) + " " + Double.toString(prev.latitude), Toast.LENGTH_LONG).show();
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(prev, myLoc).width(5).color(Color.RED));
                        prev = myLoc;
                    }

                }
            }
            Toast.makeText(m, result + "->" + Integer.toString(seconds), Toast.LENGTH_LONG).show();
        }
    }
    //@Override
    public void onMapReady() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        callAsynchronousTask();
    }
    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            getMapData task = new getMapData(MapsActivity.this.context);
                            // PerformBackgroundTask this class is the class that extends AsynchTask
                            task.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 2000); //execute in every 2000 ms
    }

}

