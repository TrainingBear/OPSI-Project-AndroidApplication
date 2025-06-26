package com.tbear9.openfarm;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

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

    public static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename) throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
