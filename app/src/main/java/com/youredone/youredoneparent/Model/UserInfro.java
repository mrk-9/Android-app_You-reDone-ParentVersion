package com.youredone.youredoneparent.Model;

/**
 * Created by 1030 on 4/16/2016.
 */
public class UserInfro {

    public String name;
    public String passcode;
    public String password;
    public String child_id;
    public long total_Time;

    public UserInfro()
    {

    }
    public UserInfro(String name,String passcode,String password,String child_id,long total_Time)
    {
        this.name = name;
        this.passcode = passcode;
        this.password = password;
        this.child_id = child_id;
        this.total_Time = total_Time;
    }

}
