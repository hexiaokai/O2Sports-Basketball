package com.o2sports.hxiao.o2sports_basketball.entity;


import java.util.Date;

/**
 * Created by hxiao on 1/28/2015.
 */
public class CheckIn {
    public String id;
    public String playerId;
    public String arenaId;
    public boolean is_registered;

    @com.google.gson.annotations.SerializedName("__createdAt")
    public Date CreatedAt;
}
