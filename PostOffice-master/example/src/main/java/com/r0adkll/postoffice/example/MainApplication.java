package com.r0adkll.postoffice.example;

import android.app.Application;

import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.model.Stamp;

/**
 * Created by r0adkll on 10/4/14.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stamp stamp = new Stamp.Builder(this)
                .setDesign(Design.MATERIAL_LIGHT)
                .setThemeColorResource(R.color.blue_500)
                .setShowKeyboardOnLaunch(true)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setIcon(R.drawable.ic_launcher)
                .build();

        PostOffice.lick(stamp);

    }
}
