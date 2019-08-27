package com.wonokoyo.wonokoyocamera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wonokoyo.wonokoyocamera.connection.RetrofitInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends AppCompatActivity {

    private static int VIDEO_REQUEST = 101;
    private Uri videoUri = null;
    private String pathToStrore;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        pd = new ProgressDialog(VideoActivity.this);
        pd.setTitle("Please Wait");
    }

    public void captureVideo(View view) {
        Intent intent =  new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, CamcorderProfile.QUALITY_LOW);
        // value dalam second bukan mili second
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, VIDEO_REQUEST);
        }
    }

    public void playVideo(View view) {
        Intent intent = new Intent(this, PlayVideoActivity.class);
        intent.putExtra("videoUri", videoUri.toString());
        startActivity(intent);
    }

    public void uploadVideoRightAway(View view) {
        uploadVideoToServer(pathToStrore);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK) {
            videoUri = data.getData();

            pathToStrore = getRealPathFromURIPath(videoUri, VideoActivity.this);
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void uploadVideoToServer(String path) {
        pd.show();

        File videoFile = new File(path);
        RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
        MultipartBody.Part vFile = MultipartBody.Part.createFormData("video", videoFile.getName(), videoBody);

        Call<ResponseBody> callUpload = RetrofitInstance.uploadService().uploadVideo(vFile);
        callUpload.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String msg = jsonObject.getString("message");

                        Toast.makeText(VideoActivity.this, msg, Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("DEBUG", "Error message " + t.getMessage());
            }
        });
    }
}
