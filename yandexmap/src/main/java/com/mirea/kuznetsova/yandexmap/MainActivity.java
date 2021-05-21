package com.mirea.kuznetsova.yandexmap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

public class MainActivity extends AppCompatActivity implements UserLocationObjectListener {
    private MapView mapView;
    private final String MAPKIT_API_KEY = "API KEY";
    private UserLocationLayer userLocationLayer;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Boolean locationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKitFactory.setApiKey(MAPKIT_API_KEY);

        MapKitFactory.initialize(this);

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionBgrd = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        int permissionCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED && permissionBgrd == PackageManager.PERMISSION_GRANTED && permissionCoarse == PackageManager.PERMISSION_GRANTED)
            locationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        mapView.getMap().move(new CameraPosition(new Point(55.751574, 37.573856),
                        11.0f, 0.0f, 0.0f), new Animation(Animation.Type.SMOOTH,
                        0), null);
        loadUserLocationLayer();

    }
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }
    private void loadUserLocationLayer(){
        MapKit mapKit = MapKitFactory.getInstance();

        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());

        userLocationLayer.setVisible(true);

        userLocationLayer.setHeadingEnabled(true);

        userLocationLayer.setObjectListener(this);
    }
    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {

        userLocationLayer.setAnchor(
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                this,android.R.drawable.star_big_on ));

        userLocationView.getPin().setIcon(ImageProvider.fromResource(
                this, android.R.drawable.ic_menu_mylocation));

    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

    }
}