package com.example.jarvis;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitAPI {
    @GET
    Call<msgModal> getmessage(@Url String url);
}
