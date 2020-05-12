package org.furion.admin.util;

import okhttp3.*;

import java.io.IOException;

public class OKHttpUtil {
    static OkHttpClient okHttpClient = new OkHttpClient();
    static MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");


    public static String get(String url){
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if(response.isSuccessful()){
                return response.message();
            }
        }catch (IOException e){
            System.out.println(e);
        }
        return "";
    }

    public static String post(String url,String body){
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, body))
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        final Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if(response.isSuccessful()){
                return response.message();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return "";
    }
}
