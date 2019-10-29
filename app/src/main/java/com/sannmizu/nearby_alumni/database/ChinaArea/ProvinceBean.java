package com.sannmizu.nearby_alumni.database.ChinaArea;

import java.util.List;

public class ProvinceBean{
    private String code;
    private String name;
    private List<CityBean> cityList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityBean> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityBean> cityList) {
        this.cityList = cityList;
    }
}
