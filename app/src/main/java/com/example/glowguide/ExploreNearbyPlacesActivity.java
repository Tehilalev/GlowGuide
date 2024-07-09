package com.example.glowguide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExploreNearbyPlacesActivity extends AppCompatActivity {

    private static final String TAG = "ExploreNearbyPlacesActivity";
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the osmdroid configuration
        Configuration.getInstance().load(getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE));

        // Set content view to activity_explore_nearby.xml
        setContentView(R.layout.activity_explore_nearby);

        // Find the MapView from layout
        mapView = findViewById(R.id.map);

        // Set the tile source to Mapnik
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Enable multi-touch controls
        mapView.setMultiTouchControls(true);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission already granted, get location
            getLastKnownLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            Log.d(TAG, "Location obtained: " + location.getLatitude() + ", " + location.getLongitude());
                            GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            mapView.getController().setZoom(15.0);
                            mapView.getController().setCenter(startPoint);

                            // Add marker to current location
                            Marker startMarker = new Marker(mapView);
                            startMarker.setPosition(startPoint);
                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            startMarker.setTitle("My Location");
                            mapView.getOverlays().add(startMarker);

                            // Find cosmetic shops nearby using Overpass API
                            findCosmeticShops(location);

                            mapView.invalidate();
                        } else {
                            Log.d(TAG, "Failed to obtain location.");
                        }
                    }
                });
    }

    private void findCosmeticShops(Location location) {
        String overpassUrl = "http://overpass-api.de/api/interpreter?data=[out:json];" +
                "(" +
                "  node[shop=cosmetics](around:20000," +
                location.getLatitude() + "," + location.getLongitude() + ");" +
                "  node[shop=makeup](around:20000," +
                location.getLatitude() + "," + location.getLongitude() + ");" +
                "  node[shop=beauty](around:20000," +
                location.getLatitude() + "," + location.getLongitude() + ");" +
                ");out;";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(overpassUrl)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e(TAG, "Overpass API request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, "Overpass API response: " + jsonResponse);
                    runOnUiThread(() -> {
                        processOverpassResponse(jsonResponse);
                    });
                } else {
                    Log.e(TAG, "Overpass API response unsuccessful: " + response.message());
                }
            }
        });
    }

    private void processOverpassResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray elements = jsonObject.getJSONArray("elements");

            for (int i = 0; i < elements.length(); i++) {
                JSONObject element = elements.getJSONObject(i);
                double lat = element.getDouble("lat");
                double lon = element.getDouble("lon");
                String name = element.has("tags") && element.getJSONObject("tags").has("name") ? element.getJSONObject("tags").getString("name") : "Cosmetic Shop";

                GeoPoint point = new GeoPoint(lat, lon);
                Marker marker = new Marker(mapView);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle(name);

                // Set custom icon
                Drawable icon = ContextCompat.getDrawable(this, R.drawable.cosmetics);
                marker.setIcon(icon);

                mapView.getOverlays().add(marker);
            }
            mapView.invalidate();

        } catch (JSONException e) {
            Log.e(TAG, "Error processing Overpass API response: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                getLastKnownLocation();
            } else {
                Log.d(TAG, "Location permission denied.");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Optional: Clean up map resources
        mapView.onDetach();
    }
}