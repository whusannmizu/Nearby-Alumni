package com.sannmizu.nearby_alumni;

import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;

import androidx.core.content.ContextCompat;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.sannmizu.nearby_alumni.denglu.Locateback;

import java.util.ArrayList;
import java.util.List;

public class Locate {
    public static Context sContext;
    public static void initialize(Context context) {
        sContext = context;
    }
    public static void requestLocation(Locateback locateback){
        //确认权限
        List< String > permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission( sContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add( Manifest.permission.ACCESS_FINE_LOCATION);
        } if (ContextCompat.checkSelfPermission( sContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add( Manifest.permission.READ_PHONE_STATE);
        } if (ContextCompat.checkSelfPermission( sContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add( Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } if (!permissionList.isEmpty()) {
            locateback.onFailure();
        } else {
            LocationClient locationClient = new LocationClient(sContext);
            initLocation(locationClient);
            locationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(final BDLocation bdLocation) {
                    String latitude, longitude;
                    latitude = String.valueOf(bdLocation.getLatitude());
                    longitude = String.valueOf(bdLocation.getLongitude());

                    int errorCode = bdLocation.getLocType();
                    if (errorCode == BDLocation.TypeCriteriaException) {
                        locateback.onFailure();
                    } else {
                        locateback.onReceiveLocation(latitude, longitude);
                    }
                }
            });
            locationClient.start();
        }
    }
    private static void initLocation(LocationClient LocationClient){
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(5000);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setIsNeedAddress(true);
        LocationClient.setLocOption(option);
    }
}
