package com.youredone.youredoneparent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youredone.youredoneparent.Model.ListViewModel;
import com.youredone.youredoneparent.common.Common;

import java.util.ArrayList;

/**
 * Created by 1030 on 4/14/2016.
 */
public class CustomList extends BaseAdapter {
    private Context context;
    private ArrayList <ListViewModel>  listViewModels;
    CheckHandler handler;

    //delegate
    public interface CheckHandler {
        //There is setcheck function's body in Certain_ScreenActivity.java file
        public void setCheck(int position, boolean status);
    }

    public CustomList(Context context,ArrayList<ListViewModel> listViewModels, CheckHandler handler)
    {
        this.context = context;
        this.listViewModels = listViewModels;
        this.handler = handler;
    }
    @Override
    public int getCount() {
        return listViewModels.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview, parent, false);
        }

        final ListViewModel listViewModel = listViewModels.get(position);
        TextView name_txt = (TextView) convertView.findViewById(R.id.name_txt);
        ImageView check_bt = (ImageView) convertView.findViewById(R.id.btn_Check);
        ImageView check_imageView = (ImageView)convertView.findViewById(R.id.check_imageView);
        check_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.setCheck(position, listViewModel.status);
            }
        });

        if (Common.commonDatas.get(position).status == true)   {  //if selected

            check_bt.setBackgroundResource(R.drawable.namebutton);
            check_imageView.setBackgroundResource(R.drawable.verify);
        }else
        if(Common.commonDatas.get(position).status == false)
        {
            check_bt.setBackgroundResource(R.drawable.buttonimage);
            check_imageView.setVisibility(View.GONE);
        }
        name_txt.setText(listViewModel.name);

        return convertView;
    }
}
