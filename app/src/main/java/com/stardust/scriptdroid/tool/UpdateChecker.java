package com.stardust.scriptdroid.tool;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stardust.scriptdroid.BuildConfig;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Stardust on 2017/4/9.
 */

public class UpdateChecker implements Response.Listener<String>, Response.ErrorListener {

    private static final String LOG_TAG = UpdateChecker.class.getName();
    private RequestQueue mRequestQueue;
    private static final String UPDATE_URL = "https://raw.githubusercontent.com/hyb1996/NoRootScriptDroid/master/version.json";

    public interface Callback {

        void onSuccess(UpdateInfo result);

        void onError(Exception exception);

    }

    public static UpdateInfo savedResult;


    private final int mTimeOut = 3000;
    private Callback mCallback;

    public UpdateChecker(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    private UpdateInfo parse(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<UpdateInfo>() {
        }.getType();
        UpdateInfo result = gson.fromJson(json, type);
        Log.i(LOG_TAG, result.toString());
        return result;
    }


    public void check(final Callback callback) {
        mCallback = callback;
        StringRequest request = new StringRequest(Request.Method.GET, UPDATE_URL, this, this);
        request.setRetryPolicy(new DefaultRetryPolicy(mTimeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag("update-check");
        request.setShouldCache(false);
        mRequestQueue.add(request);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        mCallback.onError(error);
        mCallback = null;
    }

    @Override
    public void onResponse(String response) {
        try {
            UpdateInfo result = parse(response);
            savedResult = result;
            if (mCallback == null)
                return;
            if (result.isValid()) {
                mCallback.onSuccess(result);
            } else {
                mCallback.onError(new UpdateDataInvalidException());
            }
        } catch (Exception e) {
            e.printStackTrace();
            mCallback.onError(e);
        }
        mCallback = null;
    }

    public void cancel() {
        mRequestQueue.cancelAll("update-check");
    }

    public static class UpdateInfo extends JSONObject {

        public int versionCode;
        public String releaseNotes;
        public String versionName;
        public List<Download> downloads;
        public List<OldVersion> oldVersions;
        public int deprecated;
        public String downloadUrl;

        public boolean isValid() {
            return downloads != null && !downloads.isEmpty() && versionCode > 0
                    && !TextUtils.isEmpty(versionName) && !TextUtils.isEmpty(releaseNotes);
        }

        public OldVersion getOldVersion(int versionCode) {
            for (OldVersion oldVersion : oldVersions) {
                if (oldVersion.versionCode == versionCode) {
                    return oldVersion;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "UpdateInfo{" +
                    "versionCode=" + versionCode +
                    ", releaseNotes='" + releaseNotes + '\'' +
                    ", versionName='" + versionName + '\'' +
                    ", downloads=" + downloads +
                    ", oldVersions=" + oldVersions +
                    ", deprecated=" + deprecated +
                    ", downloadUrl='" + downloadUrl + '\'' +
                    '}';
        }
    }

    public static class OldVersion extends JSONObject {

        public int versionCode;
        public String issues;

        @Override
        public String toString() {
            return "OldVersion{" +
                    "versionCode=" + versionCode +
                    ", issues='" + issues + '\'' +
                    '}';
        }
    }

    public static class Download extends JSONObject {

        public String name;
        public String url;

        @Override
        public String toString() {
            return "Download{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    public static class UpdateDataInvalidException extends RuntimeException {

    }
}
