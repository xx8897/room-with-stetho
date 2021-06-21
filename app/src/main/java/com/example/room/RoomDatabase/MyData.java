package com.example.room.RoomDatabase;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "MyTable")//這邊要先取好table的名字，稍後的table設置必須與他相同
public class MyData {


    @PrimaryKey(autoGenerate = true)//設置是否使ID自動累加
    private int id;
    private String name;
    private String country;
    private String district;
    private String road;

    public MyData(String name, String country, String district, String road) {
        this.name = name;
        this.country = country;
        this.district = district;
        this.road = road;
    }
    @Ignore//如果要使用多形的建構子，必須加入@Ignore
    public MyData(int id,String name, String country, String district, String road) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.district = district;
        this.road = road;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

}