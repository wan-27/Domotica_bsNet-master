package com.domotica.domotica_bsnet.Utils.Http_Access;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import soulissclient.Constants;


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
            Log.d(Constants.TAG, "New RequestQueue");
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        // Return RequestQueue
        return mRequestQueue;
    }

    public<T> Request<T> addRequest(Request<T> request){
        // Add the specified request to the request queue
        return getRequestQueue().add(request);
    }

    public static void JsonArrayRequest(final Context context, String ipServer, String SeverHandler,
                                        Response.Listener<JSONArray> listener, Response.ErrorListener errorListener){
        String mUrlString = "http://"+ipServer+SeverHandler;

        if(listener == null){
            listener = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Toast.makeText(mContext,response.toString(),Toast.LENGTH_LONG).show();
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

        JsonArrayRequest jsonArrReq = new JsonArrayRequest(mUrlString, listener, errorListener);

        // Add StringRequest to the RequestQueue
        getInstance(context).addRequest(jsonArrReq);
    }

    public static void JsonObjectRequest(final Context context, int method, String ipServer, String SeverHandler, JSONObject jsonObj,
                                         Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        String mUrlString = "http://"+ipServer+SeverHandler;

        if(listener == null){
            listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(mContext,response.toString(),Toast.LENGTH_LONG).show();
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

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(method, mUrlString, jsonObj, listener, errorListener)
        {
           @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               HashMap<String, String> headers = new HashMap<String, String>();
               headers.put("Content-Type", "application/json; charset=utf-8");
               return headers;
            }
        };

        // Add StringRequest to the RequestQueue
        getInstance(context).addRequest(jsonObjReq);
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

          //          getInstance(context).getRequestQueue().stop();
          //          getInstance(context).getRequestQueue().start();
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
