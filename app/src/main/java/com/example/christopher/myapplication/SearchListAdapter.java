package com.example.christopher.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Christopher on 1/29/2017.
 */

public class SearchListAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Person> searchResult;

    private LayoutInflater myInflater;
    private boolean mNotifyOnChange = true;

    public SearchListAdapter(Context cntxt, ArrayList<Person> srchRslt){
        super(cntxt, R.layout.friend_search_box);
        context = cntxt;
        myInflater = LayoutInflater.from(context);
        searchResult = srchRslt;
    }

    @Override
    public int getCount() {
        return searchResult.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return searchResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(Object item) {
        return searchResult.indexOf(item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder myHolder;
        int type = getItemViewType(position);
        if (convertView == null){
            myHolder = new ViewHolder();
            switch (type){
                case 1:
                    convertView = myInflater.inflate(R.layout.friend_search_box, parent, false);
                    myHolder.nameBox = (TextView) convertView.findViewById(R.id.searchListNameView);
                    myHolder.profilePicture = (ImageView) convertView.findViewById(R.id.searchListImageView);
                    break;
            }
            convertView.setTag(myHolder);
        }
        else{
            myHolder = (ViewHolder) convertView.getTag();
        }
        myHolder.nameBox.setText(searchResult.get(position).getName());
        myHolder.deviceID = searchResult.get(position).getDeviceID();
        myHolder.pos = position;
        return convertView;
    }

    static class ViewHolder{
        ImageView profilePicture;
        TextView nameBox;
        String deviceID;
        int pos;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    @Override
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    public void refresh(ArrayList<Person> people){
        searchResult = people;
        notifyDataSetChanged();
    }
}
