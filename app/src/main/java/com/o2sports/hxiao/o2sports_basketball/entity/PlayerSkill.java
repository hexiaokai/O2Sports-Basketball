package com.o2sports.hxiao.o2sports_basketball.entity;

/**
 * Created by hxiao on 1/26/2015.
 */
public class PlayerSkill {
    public String id;
    public String playerID;
    public int scoreCount;
    public double skill1;
    public double skill2;
    public double skill3;
    public double skill4;
    public double skill5;
    public double skill6;
    public double skill7;
    public double skill8;
    public double skill9;
    public double skill10;

    public void setDefaultScore(double score)
    {
        skill1 = skill2 = skill3 = skill4 = skill5 = skill6 = skill7 = skill8 = skill9 = skill10 = score;
    }

    public double totalScore()
    {
        return this.skill1 + this.skill2 + this.skill3 + this.skill4 + this.skill5 + this.skill6 + this.skill7 + this.skill8 + this.skill9 + this.skill10;
    }

}
