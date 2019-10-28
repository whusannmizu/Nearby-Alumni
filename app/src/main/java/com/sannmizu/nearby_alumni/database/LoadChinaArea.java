package com.sannmizu.nearby_alumni.Database;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sannmizu.nearby_alumni.Database.ChinaArea.AreaBean;
import com.sannmizu.nearby_alumni.Database.ChinaArea.CityBean;
import com.sannmizu.nearby_alumni.Database.ChinaArea.ProvinceBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

public class LoadChinaArea {
    public static void load(Context context) {
        long time1 = new Date().getTime();
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("China2019_5.json");
            InputStreamReader isReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isReader);
            String jsonLine;
            while((jsonLine = reader.readLine()) != null) {
                stringBuilder.append(jsonLine);
            }
            reader.close();
            isReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = stringBuilder.toString();
        Gson gson = new Gson();
        List<ProvinceBean> china = gson.fromJson(result, new TypeToken<List<ProvinceBean>>(){}.getType());
        Area area = null;
        //存入数据库
        for(ProvinceBean province : china) {
            String province_code = province.getCode();
            int province_id = Integer.parseInt(province_code);
            if(province_code.equals("110000") || province_code.equals("120000") || province_code.equals("310000") || province_code.equals("500000")) {

            } else {
                area = new Area();
                area.setArea_id(province_id);
                area.setName(province.getName());
                area.save();
            }
            for(CityBean city : province.getCityList()) {
                area = new Area();
                String city_code = city.getCode();
                int city_id = Integer.parseInt(city_code);
                if(city_code.equals("110000") || city_code.equals("120000") || city_code.equals("310000") || city_code.equals("500000")) {

                } else {
                    area.setPid(province_id);
                }
                area.setArea_id(city_id);
                area.setName(city.getName());
                area.save();
                for(AreaBean areaBean : city.getAreaList()) {
                    area = new Area();
                    int area_id = Integer.parseInt(areaBean.getCode());
                    area.setArea_id(area_id);
                    area.setPid(city_id);
                    area.setName(areaBean.getName());
                    area.save();
                }
            }
        }
        long time2 = new Date().getTime();
        long time = time2 - time1;
        Log.i("sannmizu.loadChina", "总耗时:" + time + "ms");
    }
}
