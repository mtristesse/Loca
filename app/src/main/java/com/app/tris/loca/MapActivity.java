package com.app.tris.loca;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements Defines {

    public static final int REQUEST_CODE = 1;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static Marker marker;


    //private double latitude = 0, longitude = 0;
    LatLng latlng = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Loca.log("map onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_map);

        Bundle args = getIntent().getExtras();
        if (args != null)
            latlng = (LatLng)args.get(LATLNG);

        setUpMapIfNeeded();

        Button buttonBack = (Button)findViewById(R.id.buttonBack);
        Button buttonUpdate = (Button)findViewById(R.id.buttonUpdate);
        if (buttonBack != null)
            buttonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Loca.log("buttonBack is pressed...");
                    finish();
                }
            });
        if (buttonUpdate != null)
            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Loca.log("buttonUpdate is pressed...");
                    //Todo: update Loca.marker here
                    Intent data = new Intent();
                    data.putExtra(LATLNG, marker.getPosition());
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
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
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        if (latlng == null) {
            Loca.log("latlng = null, use default latlng");
//        if (latitude == 0 && longitude == 0) {
            if (Loca.location != null) {
                Loca.log("using Loca location");
                latlng = new LatLng(Loca.location.getLatitude(), Loca.location.getLongitude());
            } else {    //TODO: we sould try to retrieve location
                latlng = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
            }

        } else {
            Loca.log("latlng provided");
        }

        marker = mMap.addMarker(new MarkerOptions().position(latlng));
        marker.setDraggable(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker m) {
            }

            @Override
            public void onMarkerDrag(Marker m) {
            }

            @Override
            public void onMarkerDragEnd(Marker m) {
                marker = m;
                Loca.log("onMarkerDragEnd, LatLng = " + marker.getPosition().toString());
            }
        });

    }
}
