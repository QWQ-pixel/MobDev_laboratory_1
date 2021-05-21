package com.mirea.kuznetsova.yandexdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DrivingSession.DrivingRouteListener  {
    private final Point ROUTE_START_LOCATION = new Point(55.670005, 37.479894);
    private final Point ROUTE_END_LOCATION = new Point(55.794229, 37.700772);
    private final Point SCREEN_CENTER = new Point(
            (ROUTE_START_LOCATION.getLatitude() + ROUTE_END_LOCATION.getLatitude()) / 2,
           (ROUTE_START_LOCATION.getLongitude() + ROUTE_END_LOCATION.getLongitude()) / 2);
    private MapView mapView;
    private final String MAPKIT_API_KEY = "API KEY";

    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;
    private int[] colors = {0xFFFF0000, 0xFF00FF00, 0x00FFBBBB, 0xFF0000FF};

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Boolean locationPermissionGranted;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       MapKitFactory.setApiKey(MAPKIT_API_KEY);

       MapKitFactory.initialize(this);

       DirectionsFactory.initialize(this);

       setContentView(R.layout.activity_main);

       mapView = findViewById(R.id.mapview);

       int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
       int permissionCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

       if (permissionStatus == PackageManager.PERMISSION_GRANTED && permissionCoarse == PackageManager.PERMISSION_GRANTED)
           locationPermissionGranted = true;
       else
           ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                           Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


       mapView.getMap().move(new CameraPosition(SCREEN_CENTER, 10, 0, 0));

       drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();

       mapObjects = mapView.getMap().getMapObjects().addCollection();

       submitRequest();
   }

   private void submitRequest() {
       DrivingOptions options = new DrivingOptions();

       options.setAlternativeCount(3);

       ArrayList<RequestPoint> requestPoints = new ArrayList<>();

       requestPoints.add(new RequestPoint(ROUTE_START_LOCATION, RequestPointType.WAYPOINT,
                null));

       requestPoints.add(new RequestPoint( ROUTE_END_LOCATION, RequestPointType.WAYPOINT,
                null));

       drivingSession = drivingRouter.requestRoutes(requestPoints, options, this);

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

   @Override
   public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
       int color;

       for (int i = 0; i<list.size(); i++){
           color = colors[i];
           mapObjects.addColoredPolyline(list.get(i).getGeometry()).setOutlineColor(color);
       }
   }

   @Override
   public void onDrivingRoutesError(@NonNull Error error) {
       String errorMessage = getString(R.string.unknown_error_message);

       if (error instanceof RemoteError) {
           errorMessage = getString(R.string.remote_error_message);
       }
       else if (error instanceof NetworkError) {
               errorMessage = getString(R.string.network_error_message);
           }
       Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
   }
}