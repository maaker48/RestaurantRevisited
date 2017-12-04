package com.example.stephan.restaurantrevisited;


import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends DialogFragment{

    private RestoDatabase db;
    private restuAdapter adapter;// Hoofdletter
    private Cursor cursor;// plaats cursor in functie
    private ListView lv;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        db = RestoDatabase.getInstance(getContext());
        cursor = db.selectAll();
        lv = getView().findViewById(R.id.lv_Order);
        refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View  view = inflater.inflate(R.layout.fragment_order, container, false);
        Button bt_c = view.findViewById(R.id.BT_C);
        bt_c.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                db.clear();
                refresh();
            }
        });
        Button bt_po = view.findViewById(R.id.BT_PO);
        bt_po.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                postOrderList();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    public void refresh(){
        cursor = db.selectAll();
        adapter = new restuAdapter(getContext(), cursor);
        lv.setAdapter(adapter);
    }
    private void postOrderList() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        cursor = db.totalPrice();
        cursor.moveToFirst();
        final int totalPrice = cursor.getInt(0);


        final String url = "https://resto.mprog.nl/order";


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject parsedObject = new JSONObject(response);
                            String time = parsedObject.getString("preparation_time");
                            CharSequence text = "Your order will be ready in " +time +" minutes! " +
                                    "and your order total is \u20ac"+ totalPrice;
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(getContext(), text, duration);
                            toast.show();

                            db.clear();
                            refresh();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                cursor = db.selectAll();
                while (cursor.moveToNext()){
                    params.put(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))), "");//aanpassen!

                }
                return params;
            }
        };
        queue.add(postRequest);

    }

}
