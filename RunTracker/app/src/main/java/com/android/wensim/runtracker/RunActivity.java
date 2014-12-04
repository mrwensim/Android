package com.android.wensim.runtracker;

import android.os.Bundle;
import android.app.Activity;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;


public class RunActivity extends Activity {
    MapView mMapView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.baidumap);
        mMapView = (MapView) findViewById(R.id.bmapView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
