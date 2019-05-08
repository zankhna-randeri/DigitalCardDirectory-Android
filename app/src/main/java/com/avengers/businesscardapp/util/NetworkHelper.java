package com.avengers.businesscardapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkHelper {
    private static final String TAG = "NetworkHelper";

    public static boolean hasNetworkAccess(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && (networkInfo.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return false;
        }
    }
}
