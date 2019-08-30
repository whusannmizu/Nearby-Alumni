package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @SerializedName("name")
    private String nickname;        //昵称
    @SerializedName("sign")
    private String sign;            //签名
    @SerializedName("sex")
    private String sex;             //性别
    @SerializedName("icon")
    private String icon_base64;     //头像图片的base64加密
    @SerializedName("areaId")
    private int area_id;            //所在地区id
    @SerializedName("age")
    private int age;                //年龄
    @SerializedName("constellation")
    private String constellation;   //星座
    @SerializedName("career")
    private String career;          //职业

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIcon_base64() {
        return icon_base64;
    }

    public void setIcon_base64(String icon_base64) {
        this.icon_base64 = icon_base64;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }
}
