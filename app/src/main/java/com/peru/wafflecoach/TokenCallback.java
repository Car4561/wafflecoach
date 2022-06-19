package com.peru.wafflecoach;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by culqi on 2/7/17.
 */

public interface TokenCallback {

    public void onSuccess(JSONObject token);

    public void onError(VolleyError error);

}
