package com.youredone.youredoneparent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.youredone.youredoneparent.Model.ListViewModel;
import com.youredone.youredoneparent.SQLiteDB.ListViewModel_DB_Helper;
import com.youredone.youredoneparent.SQLiteDB.UserInfoModel_DB_Helper;
import com.youredone.youredoneparent.apiModul.Constants;
import com.youredone.youredoneparent.apiModul.ParentApi;
import com.youredone.youredoneparent.common.Application;
import com.youredone.youredoneparent.common.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 1030 on 4/17/2016.
 */
public class User_detail_Two extends Activity implements ParentApi.ApiSyncHandler{

    private TextView name_display_txt2;
    private TextView passcode_display_txt2;
    private TextView amount_time2;
    private TextView remaining_time;
    private int selected_position2;
    private ListViewModel listViewModel2;
    private EditText password_edittxt2;
    private Button ok_btn2;
    private Button cancel_btn2;
    private Button alert_ok;
    private Button alert_cancel;
    private int time_hour;
    private int time_minute;
    private int time_sec;
    private Timer timer;
    final Handler handler = new Handler();
    final Handler btnHandler = new Handler();
    private RelativeLayout unlock_parAlert;
    Application instance;
    ProgressDialog progress;
    UserInfoModel_DB_Helper userDb_helper = new UserInfoModel_DB_Helper(User_detail_Two.this);
    ListViewModel_DB_Helper db_helper = new ListViewModel_DB_Helper(User_detail_Two.this);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail2);

        instance = Application.getSharedInstance();

        name_display_txt2 = (TextView)findViewById(R.id.name_display_txt2);
        passcode_display_txt2 = (TextView)findViewById(R.id.passcode_display_txt2);
        password_edittxt2 = (EditText)findViewById(R.id.password_txtedit2);
        amount_time2 = (TextView)findViewById(R.id.amount_time2);
        remaining_time = (TextView)findViewById(R.id.remain_time_txtview);
        ok_btn2 = (Button)findViewById(R.id.userdetail_OK_btn2);
        cancel_btn2 = (Button)findViewById(R.id.userdetail_cancel_btn2);
        alert_ok = (Button)findViewById(R.id.alert_ok);
        alert_cancel = (Button)findViewById(R.id.alert_cancel);
        unlock_parAlert = (RelativeLayout) findViewById(R.id.unlock_parAlert);

        unlock_parAlert.setVisibility(View.GONE);

        //set the name and passcode label using Intent
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            String value_position = bundle.getString("position2");
            selected_position2 = Integer.parseInt(value_position);
            listViewModel2 = Common.commonDatas.get(selected_position2);
            name_display_txt2.setText(listViewModel2.name);
            passcode_display_txt2.setText(listViewModel2.passcode);
        }

        ok_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password_edittxt2.getText().length() <= 0)
                    showFailedAlert("Please enter a password");
                if(password_edittxt2.getText().toString().equals(Common.commonUserInfos.get(selected_position2).password))
                {
                    unlock_parAlert.setVisibility(View.VISIBLE);
                    alert_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            progress = ProgressDialog.show(User_detail_Two.this, "Connecting to the server", "Please wait",true);
                            //Api connection
                            String url = Constants.HOST_URL + Constants.UNLOCK_URL;
                            RequestParams params = new RequestParams();
                            params.put("child_id", Common.commonUserInfos.get(selected_position2).child_id);
                            params.put("platform", "2");
                            ParentApi api = new ParentApi(url, params, User_detail_Two.this);
                            api.syncObject();
                        }
                    });
                    alert_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                         unlock_parAlert.setVisibility(View.GONE);
                        }
                    });
                }
                else
                {
                    showFailedAlert("Password incorrect");
                }

            }
        });
        cancel_btn2 = (Button)findViewById(R.id.userdetail_cancel_btn2);
        cancel_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.flag_alert = 0;

                Long cur_seconds = System.currentTimeMillis()/1000;
                Log.d("Current time",String.valueOf(cur_seconds));
                long cur_amount_time = time_hour * 3600 + time_minute * 60 + time_sec;
                long end_time = cur_seconds + cur_amount_time * 60;
                onBackPressed();
            }
        });

        Long cur_seconds = System.currentTimeMillis()/1000;
        Log.d("Current time",String.valueOf(cur_seconds));

        long difference_time = Common.commonUserInfos.get(selected_position2).total_Time - cur_seconds;
         //Set the downcounter
        if (difference_time > 0) {
            time_hour = (int) difference_time / 3600;
            time_minute = (int) (difference_time%3600) / 60;
            time_sec = (int) difference_time % 60;
            timer = new Timer();

            MyTimerTask myTimerTask = new MyTimerTask();
            timer.schedule(myTimerTask, new Date(), 1000);
        } else {
            amount_time2.setText(String.format("%02d",time_hour) + " : " + String.format("%02d",time_minute) + " : " + String.format("%02d",time_sec));
            remaining_time.setText(String.format("%02d",time_hour) + " : " + String.format("%02d",time_minute) + " : " + String.format("%02d",time_sec));
        }
    }
    private void showSuccessAlert(String message)  {
        AlertDialog alertDialog = new AlertDialog.Builder(User_detail_Two.this).create();
        alertDialog.setTitle("Help");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    private void showFailedAlert(String message)  {
        AlertDialog alertDialog = new AlertDialog.Builder(User_detail_Two.this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    //Display counterdown
    class MyTimerTask extends TimerTask
    {
        @Override
        public void run() {

            if(time_hour == 0 && time_minute == 0 && time_sec == 0)
            {
                btnHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        timer.cancel();
                        timer = null;
                    }
                });
            }
            if((time_hour > 0 || time_minute >= 0) && time_sec >= 0)
            {
                if(time_sec == 0)
                {
                    if(time_minute == 0)
                    {
                        time_hour -= 1;
                        time_minute = 59;
                    }else if(time_minute > 0)
                    {
                        time_minute -= 1;
                    }
                    time_sec = 59;
                }
                else if(time_sec > 0)
                {
                    time_sec -= 1;
                }
                if(time_hour > -1) {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            amount_time2.setText(String.format("%02d",time_hour) + " : " + String.format("%02d",time_minute) + " : " + String.format("%02d",time_sec));
                            remaining_time.setText(String.format("%02d",time_hour) + " : " + String.format("%02d",time_minute) + " : " + String.format("%02d",time_sec));
                        }
                    });
                }
            }

        }
    }

    //if result with connecting Api is success
    @Override
    public void success(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("status").equals("OK"))
            {
                Common.commonDatas.get(selected_position2).status = false;
                db_helper.update_model(Common.commonDatas.get(selected_position2));
                progress.dismiss();
                onBackPressed();
            }else
            {
                showFailedAlert("Sorry.failed.Please confirm if exist this child again.");
                progress.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //if result with connecting APi is failed
    @Override
    public void failed(String response, Throwable throwable) {
        showFailedAlert("Please check network status.");
        progress.dismiss();
    }
}
