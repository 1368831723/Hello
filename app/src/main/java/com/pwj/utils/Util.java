package com.pwj.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by han on 2018/9/21.
 */

public class Util {

    private static Toast toast;

    public static void showToast(Context context,
                                 String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

}
