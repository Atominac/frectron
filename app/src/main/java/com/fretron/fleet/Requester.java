package com.fretron.fleet;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import org.json.JSONArray;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Requester extends JsonRequest<JSONArray> {
    public Requester(int method, String url, JSONObject requestBody, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, String.valueOf(requestBody), listener, errorListener);
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String jsonString = new String(networkResponse.data,
                    HttpHeaderParser
                            .parseCharset(networkResponse.headers));
            return Response.success(new JSONArray(jsonString),
                    HttpHeaderParser
                            .parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }

    }
}
