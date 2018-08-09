package com.india.kalam.firebasetictactoe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class UserAdapter extends ArrayAdapter {

    public UserAdapter(Context context, ArrayList<User> users)
    {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        User user = (User)getItem(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_list, parent, false);
        }

        TextView text1  = (TextView) convertView.findViewById(R.id.user_id);
        TextView text2 = (TextView) convertView.findViewById(R.id.name);
        TextView text3 = (TextView) convertView.findViewById(R.id.emailAddress);

        text1.setText(user.myID);
        text2.setText(user.name);
        text3.setText(user.email);

        return convertView;
    }
}
