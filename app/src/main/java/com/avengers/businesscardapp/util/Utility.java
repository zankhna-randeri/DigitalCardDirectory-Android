package com.avengers.businesscardapp.util;

import android.content.Context;
import android.widget.Toast;

public class Utility {
    private static final Utility ourInstance = new Utility();

    public static synchronized Utility getInstance() {
        return ourInstance;
    }

    private Utility() {
    }

    public void showMsg(Context context, String msg) {
        Toast.makeText(context, msg,
                Toast.LENGTH_SHORT).show();
    }

    public String getInitials(String name) {
        String[] names = name.split(" ");
        if (names.length > 0) {
            String initials = (names.length > 1) ? "" + names[0].charAt(0) + names[1].charAt(0) :
                    "" + names[0].charAt(0);
            return initials;
        }
        return "";
    }
}
