package com.TBear9.openfarm;

import android.util.Log;

public final class Util {
    public static void debug(Object... msg){
        StringBuilder messages = new StringBuilder();
        for (Object o : msg) {
            messages.append(o.toString());
        }
        Log.d("ODebug", messages.toString());
    }
}
