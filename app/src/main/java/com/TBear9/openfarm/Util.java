package com.TBear9.openfarm;

import android.util.Log;

public final class Util {


    public static void d(Class<?> clazz, Object... msg) {
        debug(clazz.getSimpleName(), msg);
    }
    public static void debug(Object... msg) {
        debug("OpenFarm", msg);
    }
    public static void debug(String name, Object... msg){
        StringBuilder messages = new StringBuilder();
        for (Object o : msg) {
            messages.append(o.toString());
        }
        Log.d(name, messages.toString());
    }
}
