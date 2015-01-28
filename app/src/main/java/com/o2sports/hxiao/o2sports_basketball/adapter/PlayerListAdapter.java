package com.o2sports.hxiao.o2sports_basketball.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.o2sports.hxiao.o2sports_basketball.R;
import com.o2sports.hxiao.o2sports_basketball.entity.Player;

/**
 * Created by Xiaokai He on 1/14/2015.
 */
public class PlayerListAdapter extends ArrayAdapter<Player> {

    private Context mContext;

    public PlayerListAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final Player currentPlayer = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.list_view_button, parent, false);
        }

        row.setTag(currentPlayer);


        final Button playerButton = (Button) row.findViewById(R.id.listButton);
        playerButton.setTag(currentPlayer);
        playerButton.setText(currentPlayer.name);
        playerButton.setEnabled(true);
        playerButton.setOnClickListener((View.OnClickListener)mContext);

        return row;
    }
}
