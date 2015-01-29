package com.o2sports.hxiao.o2sports_basketball.entity;


import java.util.Date;

/**
 * Created by hxiao on 1/28/2015.
 */
public class Registration {

    public String id;

    public String playerId;

    public String arenaId;

    @com.google.gson.annotations.SerializedName("startTime")
    public Date StartTime;

    @com.google.gson.annotations.SerializedName("endTime")
    public Date EndTime;
}
