package com.example.stephan.restaurantrevisited;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends ListFragment {

    public List<String> menuItems = new ArrayList<>();
    public ArrayAdapter adapter;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String s =l.getItemAtPosition(position).toString();
        Log.d("String s", s);
        MenuFragment menuFragment = new MenuFragment();

        Bundle args = new Bundle();
        args.putString("category", s);
        menuFragment.setArguments(args);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, menuFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        final String url = "https://resto.mprog.nl/categories";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray categories  = response.getJSONArray("categories");
                            Log.d("json", categories.toString());
                            for (int i = 0; i < categories.length(); i++) {
                                menuItems.add(categories.get(i).toString());
                            }
                            updateAdaptor();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        Log.d("jsonurl", getRequest.toString());

        // add it to the RequestQueue
        queue.add(getRequest);

    }

    public void updateAdaptor(){
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, menuItems);
        Log.d("menu", menuItems.toString());
        this.setListAdapter(adapter);

    }
}
