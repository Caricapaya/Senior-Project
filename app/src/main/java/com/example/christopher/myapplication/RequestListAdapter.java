package com.example.christopher.myapplication;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Christopher on 12/5/2016.
 * Code from http://stackoverflow.com/questions/14013833/populating-a-custom-listview-from-listarray
 */
//TODO FINISH THIS THING
public class RequestListAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Person> pendingRequests;

    private LayoutInflater myInflater;
    private boolean mNotifyOnChange = true;

    public RequestListAdapter(Context cntxt, ArrayList<Person> requests){
        super(cntxt, R.layout.friend_request_box);
        context = cntxt;
        pendingRequests = requests;
        myInflater = LayoutInflater.from(cntxt);
    }

    @Override
    public int getCount() {
        return pendingRequests.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return pendingRequests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
