package com.android.wensim.smartlock;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Map;

public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList<LatLng>>
{
    public static final String USER_CURRENT_LAT = "user_current_lat";
    public static final String USER_CURRENT_LONG = "user_current_long";
    public static final String DESTINATION_LAT = "destination_lat";
    public static final String DESTINATION_LONG = "destination_long";
    public static final String DIRECTIONS_MODE = "directions_mode";
    private MyMapFragment myMapFragment;
    private Exception exception;
    private ProgressDialog progressDialog;
 
    public GetDirectionsAsyncTask(MyMapFragment fragment)
    {
        super();
        this.myMapFragment = fragment;
    }
 
    public void onPreExecute()
    {
        progressDialog = new ProgressDialog(myMapFragment.getActivity());
        progressDialog.setMessage("Calculating directions");
        progressDialog.show();
    }
 
    @Override
    public void onPostExecute(ArrayList result)
    {
        progressDialog.dismiss();
        if (exception == null)
        {
            myMapFragment.handleGetDirectionsResult(result);
        }
        else
        {
            processException();
        }
    }
 
    @Override
    protected ArrayList<LatLng> doInBackground(Map<String, String>... params)
    {
        Map<String, String> paramMap = params[0];
        try
        {
            LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)) , Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
            LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));
            GMapV2Direction md = new GMapV2Direction();
            Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
            ArrayList<LatLng> directionPoints = md.getDirection(doc);
            return directionPoints;
        }
        catch (Exception e)
        {
            exception = e;
            return null;
        }
    }
 
    private void processException()
    {
        Toast.makeText(myMapFragment.getActivity(), "Error retriving data", 3000).show();
    }
}