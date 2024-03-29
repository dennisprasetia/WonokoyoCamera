package com.wonokoyo.wonokoyocamera.connection;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadService {
    @Multipart
    @POST(Vars.UPLOAD_VIDEO)
    Call<ResponseBody> uploadVideo(@Part MultipartBody.Part video);
}
