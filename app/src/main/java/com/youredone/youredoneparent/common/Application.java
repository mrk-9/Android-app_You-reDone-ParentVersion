package com.youredone.youredoneparent.common;

/**
 * Created by 1030 on 4/17/2016.
 */
public class Application {
    public static Application instance = null;
    public static String child_id;
    public static int flag_alert;   //identify which UI
    public static Application getSharedInstance()
    {
        if(instance == null)
        {
            instance = new Application();
        }
        return instance;
    }
    public Application()
    {

    }
}
