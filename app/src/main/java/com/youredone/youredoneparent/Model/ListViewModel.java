package com.youredone.youredoneparent.Model;

/**
 * Created by 1030 on 4/13/2016.
 */
public class ListViewModel{
    public String name;
    public String passcode;
    public boolean status;
    public String child_id;


    public ListViewModel()
    {

    }
    public ListViewModel(String name,String passcode,String child_id)
    {
        this.name = name;
        this.passcode = passcode;
        this.status = false;
        this.child_id = child_id;

    }
}
