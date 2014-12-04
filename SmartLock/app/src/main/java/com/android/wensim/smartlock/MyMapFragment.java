package com.android.wensim.smartlock;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link android.app.Fragment} subclass.
 *
 */
public class MyMapFragment extends Fragment {
    final Double fromLat = 30.515904986052377;
    final Double fromLng = 114.43257177449233;
    MapView mapView;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    Button mButtonSend;
    private EditText mEditTextLat,mEditTextLng;
    Double toLatitude,toLongitude;
    private Polyline newPolyline;
    private LatLngBounds latlngBounds;
    public static MyMapFragment newInstance() {
        MyMapFragment myMapFragment = new MyMapFragment();
        return myMapFragment;
    }
    public MyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView)view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        googleMap = mapView.getMap();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMyLocationEnabled(true);
        MapsInitializer.initialize(this.getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //默认标记
        markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(fromLat, fromLng));
        markerOptions.title("韵苑12栋613");
        markerOptions.draggable(false);
        markerOptions.visible(true);
        markerOptions.anchor(0.5f, 0.5f);
        googleMap.addMarker(markerOptions);

        mEditTextLat = (EditText)getView().findViewById(R.id.et_latitude);
        mEditTextLng = (EditText)getView().findViewById(R.id.et_longitude);
        mButtonSend = (Button)getView().findViewById(R.id.btn_send_loc);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.clear();
                toLatitude = Double.parseDouble(mEditTextLat.getText().toString());
                toLongitude = Double.parseDouble(mEditTextLng.getText().toString());
                googleMap.addMarker(new MarkerOptions().position(new LatLng(toLatitude, toLongitude)));
                //查找路线
                findDirections(fromLat, fromLng,toLatitude, toLongitude, GMapV2Direction.MODE_WALKING);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
        PolylineOptions rectLine = new PolylineOptions().width(5).color(Color.RED);

        for(int i = 0 ; i < directionPoints.size() ; i++)
        {
            rectLine.add(directionPoints.get(i));
        }
        if (newPolyline != null)
        {
            newPolyline.remove();
        }
        newPolyline = googleMap.addPolyline(rectLine);
        latlngBounds = createLatLngBoundsObject(new LatLng(fromLat,fromLng), new LatLng(toLatitude, toLongitude));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, 150));
    }

    /**
     * 创建一个LatLngBounds对象(一个包含两个坐标的最小矩形区域)
     * @param firstLocation
     * @param secondLocation
     * @return
     */
    private LatLngBounds createLatLngBoundsObject(LatLng firstLocation, LatLng secondLocation)
    {
        if (firstLocation != null && secondLocation != null)
        {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(firstLocation).include(secondLocation);

            return builder.build();
        }
        return null;
    }

    public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(map);
    }
}
