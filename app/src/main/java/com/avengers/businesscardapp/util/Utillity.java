package com.avengers.businesscardapp.util;

import android.content.Context;
import android.widget.Toast;

public class Utillity {

    public static void showMsg(Context context, String msg) {
        Toast.makeText(context, msg,
                Toast.LENGTH_SHORT).show();
    }

}
