package com.youredone.youredoneparent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

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

import java.util.ArrayList;

/**
 * Created by 1030 on 4/13/2016.
 */                                                             //same as delegate in iOS
public class Certain_ScreenActivity extends Activity implements CustomList.CheckHandler,ParentApi.ApiSyncHandler {
    private RelativeLayout hidden_relative;
    private LinearLayout remove_parlayout;
    private Button okButton;
    private Button cancelButton;
    private Button addButton;
    private Button deleteButton;
    private Button remove_OK;
    private Button remove_Cancel;
    private Button remove_btn;
    private EditText name_edittxt;
    private EditText passcode_edittxt;
    private ListView listView;
    private ArrayList <ListViewModel> dataList = new ArrayList<>();
    private int cell_position = -1;
    ProgressDialog progress;
    Application instance;
    ListViewModel_DB_Helper db_helper = new ListViewModel_DB_Helper(this);
    UserInfoModel_DB_Helper user_helper = new UserInfoModel_DB_Helper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.certain_screen);

        instance = Application.getSharedInstance();

        listView = (ListView)findViewById(R.id.listView);
        addButton = (Button)findViewById(R.id.add_bttn);
        remove_btn = (Button)findViewById(R.id.delete_btn);
        deleteButton = (Button)findViewById(R.id.delete_btn);
        okButton = (Button)findViewById(R.id.ok_btn);
        remove_OK = (Button)findViewById(R.id.remove_OK_btn);
        remove_Cancel = (Button)findViewById(R.id.remove_cancel_btn);
        cancelButton = (Button)findViewById(R.id.cancel_btn);
        name_edittxt = (EditText)findViewById(R.id.name_edittxt);
        passcode_edittxt = (EditText)findViewById(R.id.passcode_edittxt);
        remove_parlayout = (LinearLayout)findViewById(R.id.remove_parLayout);
        hidden_relative = (RelativeLayout)findViewById(R.id.hidden_relative);
        hidden_relative.setVisibility(View.GONE);
        remove_parlayout.setVisibility(View.GONE);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidden_relative.setVisibility(View.VISIBLE);
                name_edittxt.setText("");
                passcode_edittxt.setText("");
            }
        });
        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.commonDatas == null)
                    Common.commonDatas = new ArrayList<ListViewModel>();
                if(Common.commonDatas.size() == 0)
                    showFailedAlert("There are no phones to choose from");
                else if(cell_position == -1)
                 showFailedAlert("Please select a phone");
                else if(Common.commonDatas.get(cell_position).status == true)
                    showFailedAlert("Sorry.You can't remove this phone.Initially unlock this phone.");
                else
                {
                    remove_parlayout.setVisibility(View.VISIBLE);
                    remove_OK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Array SIZE",String.valueOf(Common.commonDatas.size()));

                            //Delete the kid's info from localmemory
                            db_helper.delete_model(Common.commonDatas.get(cell_position));
                            user_helper.delete_model(Common.commonUserInfos.get(cell_position));

                            Common.commonDatas.remove(cell_position);

                            //reload listView
                            connectAdapter(Common.commonDatas);
                            cell_position = -1;
                            remove_parlayout.setVisibility(View.GONE);
                        }
                    });
                    remove_Cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            remove_parlayout.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize the Common.commonDatas
                if (Common.commonDatas == null)
                    Common.commonDatas = new ArrayList<ListViewModel>();

                if(name_edittxt.getText().length() <= 0)
                    showFailedAlert("Please enter valid name");
                else if(passcode_edittxt.getText().length() <= 0)
                    showFailedAlert("Please enter valid passcode");
                //Identify the number of child phone using common.commonDatas.size
                else if(Common.commonDatas.size() >= 5) {
                    showFailedAlert("You can set the phone of up 5 children");
                }else {
                    progress = ProgressDialog.show(Certain_ScreenActivity.this, "Connecting to the server", "Please wait",true);
                    //Api connection
                    String url = Constants.HOST_URL + Constants.LOGIN_URL;
                    RequestParams params = new RequestParams();
                    params.put("name", name_edittxt.getText().toString());
                    params.put("passcode", passcode_edittxt.getText().toString());
                    ParentApi api = new ParentApi(url, params, Certain_ScreenActivity.this);
                    api.syncObject();
                    //hidden keyboard
                    hideSoftKeyboard();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hidden keyboard
                hideSoftKeyboard();
                hidden_relative.setVisibility(View.GONE);
            }
        });
        //when press the listview item change the background color
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            view.setSelected(true);
            cell_position = position;
            Log.d("Cell_Position",String.valueOf(cell_position));
        }
    });

        //Call the localmemory and show the kid's info after phone restart
        Common.commonDatas = db_helper.getAllModels();

        if (Common.commonDatas == null) {
            Common.commonDatas = new ArrayList<>();
        }

        //call the localmemory and save kid's info(name,passcode,password,child_id and so on after phone restart)
        Common.commonUserInfos = user_helper.getAllUserInfos();

        if (Common.commonUserInfos == null) {
            Common.commonUserInfos = new ArrayList<>();
        }
    }

    private void showSuccessAlert(String message)  {
        AlertDialog alertDialog = new AlertDialog.Builder(Certain_ScreenActivity.this).create();
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
        AlertDialog alertDialog = new AlertDialog.Builder(Certain_ScreenActivity.this).create();
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
    //hidden keyboard function
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    //Delegate Body where define in CustomList
    @Override
    public void setCheck(int position, boolean status) {
        Log.d("position", position + "");
        //Initialize ArrayList Common.commonUserInfo
        if (Common.commonDatas == null)
            Common.commonDatas = new ArrayList<ListViewModel>();

        if(Common.commonDatas.get(position).status == false) {       //if unselected
            Intent intent = new Intent(Certain_ScreenActivity.this, User_detail.class);
            intent.putExtra("position", String.valueOf(position));
            startActivity(intent);
        }else
        {
            Intent intent = new Intent(Certain_ScreenActivity.this,User_detail_Two.class);
            intent.putExtra("position2",String.valueOf(position));
            startActivity(intent);
        }
    }
    //ListView reload & listView connect with Adapter
    public void connectAdapter(ArrayList<ListViewModel> list)   {
        if (listView.getAdapter() == null)  {
            listView.setAdapter(null);
        }

        listView.setAdapter(new CustomList(Certain_ScreenActivity.this, list, Certain_ScreenActivity.this));
    }

    //Disable back button
    @Override
    public void onBackPressed() {
    }

    //Disable back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    //This function is called when Transfer other UI using Intent and come back again
    @Override
    protected void onResume() {
        super.onResume();
        if (Common.commonDatas != null) {
            connectAdapter(Common.commonDatas);
        }
        if(instance.flag_alert == 1)
        {
            showSuccessAlert("Please check the notification on your kid's phone.");
        }
    }

    //if result with connecting Api is success
    @Override
    public void success(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("status").equals("OK"))
            {
                //Get Child_id
                String child_id  = jsonObject.getJSONArray("data").getJSONObject(0).getString("id");
//                instance.child_id = child_id;
                ListViewModel listViewModel = new ListViewModel(name_edittxt.getText().toString(),passcode_edittxt.getText().toString(),child_id);

                //Save to Localmemory using SQLite
                db_helper.add_listViewModel(listViewModel);

                Common.commonDatas.add(listViewModel);
                //Connect Adapter
                connectAdapter(Common.commonDatas);
                //hidden keyboard
                hideSoftKeyboard();

                progress.dismiss();
                hidden_relative.setVisibility(View.GONE);
            }else
            {
                showFailedAlert("Can not find a user in server.Had you registered a user on child phone?");
                progress.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //if result with connecting APi is failed
    @Override
    public void failed(String response, Throwable throwable) {
        showFailedAlert("Please check network state.");
        progress.dismiss();
    }
}
