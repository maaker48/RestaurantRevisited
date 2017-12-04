package com.example.stephan.restaurantrevisited;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

/**
 * Created by Stephan on 3-12-2017.
 */

public class restuAdapter extends ResourceCursorAdapter {
    public restuAdapter(Context context, Cursor cursor){
        super(context , R.layout.row_oder, cursor);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView count = view.findViewById(R.id.tvCount);
        TextView name = view.findViewById(R.id.tvname);
        name.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
//        price.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
        count.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));
    }
}
