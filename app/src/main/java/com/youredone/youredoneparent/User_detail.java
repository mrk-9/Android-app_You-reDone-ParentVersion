package com.youredone.youredoneparent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.loopj.android.http.RequestParams;
import com.youredone.youredoneparent.Model.ListViewModel;
import com.youredone.youredoneparent.Model.UserInfro;
import com.youredone.youredoneparent.SQLiteDB.ListViewModel_DB_Helper;
import com.youredone.youredoneparent.SQLiteDB.UserInfoModel_DB_Helper;
import com.youredone.youredoneparent.apiModul.Constants;
import com.youredone.youredoneparent.apiModul.ParentApi;
import com.youredone.youredoneparent.common.Application;
import com.youredone.youredoneparent.common.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

/**
 * Created by 1030 on 4/16/2016.
 */
public class User_detail extends Activity implements ParentApi.ApiSyncHandler{

    private EditText time_edit_txt;
    private TextView name_display_txt;
    private TextView passcode_display_txt;
    private TextView amount_time;
    private int hour_txt;
    private int minute_txt;
    private int second_txt = 0;
    private int total_Time;
    private int selected_position;
    private EditText password_txtedit;
    private EditText repassword_txtedit;
    private Button userdetail_OK_btn;
    private Button userdetail_cancel_btn;
    private ListViewModel listViewModel;
    private CustomList.CheckHandler checkHandler;
    private Timer timer;
    private UserInfro selected_userInfo;
    final Handler handler = new Handler();
    final Handler btnHandler = new Handler();
    Application instance;
    ProgressDialog progress;
    ListViewModel_DB_Helper db_helper = new ListViewModel_DB_Helper(User_detail.this);
    UserInfoModel_DB_Helper userDb_helper = new UserInfoModel_DB_Helper(User_detail.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);

        instance = Application.getSharedInstance();

        name_display_txt = (TextView)findViewById(R.id.name_display_txt);
        passcode_display_txt = (TextView)findViewById(R.id.passcode_display_txt);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            //Set the name and passcode label from Certain_ScreenActivity using intent
            String value_string = bundle.getString("position");
            selected_position = Integer.parseInt(value_string);
            listViewModel = Common.commonDatas.get(selected_position);
            name_display_txt.setText(listViewModel.name);
            passcode_display_txt.setText(listViewModel.passcode);
        }
        //Set the timepicker of edit text
        time_edit_txt = (EditText)findViewById(R.id.time_edittxt);
        //Set the string on center of Edit text
        time_edit_txt.setGravity(Gravity.CENTER_HORIZONTAL);
        time_edit_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(User_detail.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time_edit_txt.setText( selectedHour + ":" + selectedMinute);
                        hour_txt = selectedHour;
                        minute_txt = selectedMinute;
                        total_Time = hour_txt * 60 + minute_txt;

                        amount_time = (TextView)findViewById(R.id.amount_time);
                        amount_time.setText(String.format("%02d",hour_txt) + " : " + String.format("%02d",minute_txt) + " : " + "00");
                        Log.d("hh :: dd", hour_txt + " : " + minute_txt);
                    }
                },hour_txt,minute_txt,true);//Yes 24 hour time
                mTimePicker.setTitle("");
                mTimePicker.show();
                //hidden keyboard
                hideSoftKeyboard();
            }
        });

        password_txtedit = (EditText)findViewById(R.id.password_txtedit);
        repassword_txtedit = (EditText)findViewById(R.id.repassword_txtedit);
        userdetail_OK_btn = (Button)findViewById(R.id.userdetail_OK_btn);
        userdetail_cancel_btn = (Button)findViewById(R.id.userdetail_cancel_btn);

        userdetail_OK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password_txtedit.getText().length() < 5)
                    showFailedAlert("Passwords must have at least 5 characters");
                else if(password_txtedit.getText().length() <= 0)
                    showFailedAlert("Please enter password");
                else if(repassword_txtedit.getText().length() <= 0)
                    showFailedAlert("Please reenter password");
                else if(password_txtedit.getText().toString().equals(repassword_txtedit.getText().toString()))
                {
                    progress = ProgressDialog.show(User_detail.this, "Connecting to the server", "Please wait",true);
                    //Api connection
                    String url = Constants.HOST_URL + Constants.LOCK_URL;
                    RequestParams params = new RequestParams();
                    params.put("child_id", Common.commonDatas.get(selected_position).child_id);
                    params.put("platform", "2");
                    params.put("locktime",String.valueOf(total_Time));
                    ParentApi api = new ParentApi(url, params, User_detail.this);
                    api.syncObject();
                }
                else
                {
                    showFailedAlert("These passwords don't match.Please reenter password");
                }
            }
        });
        userdetail_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.flag_alert = 0;
                onBackPressed();
            }
        });

        //Call the localmemory and show the kid's info after phone restart
        Common.commonDatas = db_helper.getAllModels();

        if (Common.commonDatas == null) {
            Common.commonDatas = new ArrayList<>();
        }
    }

    private void showSuccessAlert(String message)  {
        AlertDialog alertDialog = new AlertDialog.Builder(User_detail.this).create();
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
        AlertDialog alertDialog = new AlertDialog.Builder(User_detail.this).create();
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

    //if result with connecting Api is success
    @Override
    public void success(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("status").equals("OK"))
            {
                //Initialize the Common.commonDatas
                if (Common.commonUserInfos == null)
                    Common.commonUserInfos = new ArrayList<UserInfro>();

                if (selected_position < Common.commonUserInfos.size()) {
                    selected_userInfo = Common.commonUserInfos.get(selected_position);
                } else
                    selected_userInfo = new UserInfro();
                selected_userInfo.name = name_display_txt.getText().toString();
                selected_userInfo.passcode = passcode_display_txt.getText().toString();
                selected_userInfo.password = password_txtedit.getText().toString();
                selected_userInfo.child_id = Common.commonDatas.get(selected_position).child_id;

                Long cur_seconds = System.currentTimeMillis()/1000;
                Log.d("Current time",String.valueOf(cur_seconds));
                long cur_amount_time = total_Time * 60;
                long end_time = cur_seconds + cur_amount_time;
                selected_userInfo.total_Time = end_time;

                userDb_helper.add_UserInfoModel(selected_userInfo);
                Common.commonUserInfos.add(selected_userInfo);

                //Identify select or unselect using setcheck function
                Common.commonDatas.get(selected_position).status = true;
                //Save updated ListViewModel to localMemory
                db_helper.update_model(Common.commonDatas.get(selected_position));

                //insert individual user information to CommonUserInfo in Common
                progress.dismiss();

                //To show alert on Certain_ScreebActivity OnResume
                instance.flag_alert = 1;
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

    //hidden keyboard function
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
