package com.sannmizu.nearby_alumni.database;

import android.util.Base64;

import com.sannmizu.nearby_alumni.NetUtils.User;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

/*表Users，用来在本地存储部分加载过的用户的信息*/
public class Users extends LitePalSupport {
    private int id;
    @Column(unique = true, nullable = false)
    private int user_id;
    @Column(nullable = false)
    private String nickname;
    private byte[] icon;
    private String sign;
    private String sex;
    private int area_id;
    private String career;
    private String constellation;
    private int age;

    private Date updateTime;

    public Users() {

    }

    public Users(User user) {
        super();
        this.user_id = user.getId();
        this.nickname = user.getInfo().getNickname();
        if(user.getInfo().getIcon_base64() != null) {
            this.icon = Base64.decode(user.getInfo().getIcon_base64(), Base64.NO_WRAP);
        } else {
            this.icon = null;
        }
        this.sign = user.getInfo().getSign();
        this.sex = user.getInfo().getSex();
        this.area_id = user.getInfo().getArea_id();
        this.career = user.getInfo().getCareer();
        this.constellation = user.getInfo().getConstellation();
        this.age = user.getInfo().getAge();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int friend_id) {
        this.user_id = friend_id;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
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

    public int getArea() {
        return area_id;
    }

    public void setArea(int area_id) {
        this.area_id = area_id;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }
}
