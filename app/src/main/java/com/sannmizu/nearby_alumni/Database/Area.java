package com.sannmizu.nearby_alumni.Database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Area extends LitePalSupport {
    @Column(nullable = false)
    private int area_id;
    private int pid;
    @Column(nullable = false)
    private String name;

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
