package org.furion.core.protocol.client;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.furion.core.context.FurionResponse;
import org.furion.core.protocol.client.http.HttpNetWork;
import org.furion.core.utils.OkHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class OKHttpClientNetWork implements HttpNetWork<Request, FurionResponse<Response>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OKHttpClientNetWork.class);

    private static final OkHttpClient okHttpClient = OkHttpUtil.getOkHttpClient();

    @Override
    public FurionResponse<Response> send(Request request) {



        RequestBody requestBody = new FormBody.Builder()
                .add("search", "Jurassic Park")

                .build();
        Request request0 = new Request.Builder()
                .url("")

                .post(requestBody)
                .build();

        okHttpClient.newCall(request0).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                LOGGER.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                LOGGER.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
//                    LOGGER.d(TAG, headers.name(i) + ":" + headers.value(i));
                }
//                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
        return null;
    }

    private void post() {

        RequestBody requestBody = new FormBody.Builder()
                .add("search", "Jurassic Park")
                .build();
        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/index.php")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
//                    Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                }
//                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    private void get() {

        final Request request = new Request.Builder()
//                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }
}
