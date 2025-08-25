package com.tbear9.openfarm.api;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

public final class Handler {
    public static void post(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().post(
                new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.parse("s");
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                    }
                }
        ).build();
    }
}
