package com.domotica.domotica_bsnet.Utils.Http_Access;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class MyVolley {
    private static MyVolley mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private MyVolley(Context context){
        // Specify the application context
        mContext = context;
        // Get the request queue
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyVolley getInstance(Context context){
        // If Instance is null then initialize new Instance
        if(mInstance == null){
            mInstance = new MyVolley(context);
        }
        // Return MyVolley new Instance
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        // If RequestQueue is null the initialize new RequestQueue
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        // Return RequestQueue
        return mRequestQueue;
    }

    public<T> Request<T> addRequest(Request<T> request){
        // Add the specified request to the request queue
        return getRequestQueue().add(request);
    }

    public static void ServerRequest(final Context context, String ipServer, String SeverHandler, int method,
                                              Response.Listener<String> listener, Response.ErrorListener errorListener,
                                     final Map<String,String> params){
        String mUrlString = "http://"+ipServer+SeverHandler;

        if(listener == null){
            listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(mContext,response,Toast.LENGTH_LONG).show();
                }
            };
        }

        if(errorListener == null){
            errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, error.toString(),Toast.LENGTH_LONG).show();
                }
            };
        }

        StringRequest stringRequest = new StringRequest(
                method, mUrlString, listener, errorListener){
            @Override
            protected Map<String,String> getParams(){
                /*
                Map<String,String> params = new HashMap<String, String>();
                params.put("devicename",username);
                */
                return params;
                //return null;
            }

        };

        // Add StringRequest to the RequestQueue
        getInstance(context).addRequest(stringRequest);

        //return stringRequest;
    }
}
