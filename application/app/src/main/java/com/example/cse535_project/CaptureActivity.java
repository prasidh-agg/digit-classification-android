package com.example.cse535_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import cz.msebera.android.httpclient.Header;

@SuppressWarnings("ALL")
public class CaptureActivity extends PreProcessImage {
    // Add local IP address here in SERVER_URL
    public static final String SERVER_URL = "http://172.20.10.4:80";

    ImageView imageView;
    String finalFileName;

    public Uri imageCaptureUrl;
    int CAMERA_PIC_REQUEST = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        imageView = (ImageView) findViewById(R.id.imageView);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_acitivity);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageCaptureUrl);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }

    // This method will help to retrieve the image
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Bitmap finalImage = PreProcessImage.greyscale(photo);
            imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(photo);
            Log.i("CAPTURE", String.valueOf(imageCaptureUrl));
            String root = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DCIM + "/";
            File myDir = new File(root);
            myDir.mkdirs();
            String fileName = System.currentTimeMillis
                    () + "_image.jpeg";
            File file = new File(myDir, fileName);
            Log.i("CAPTURE", root + fileName);
            finalFileName = root + fileName;
            try {
                FileOutputStream out = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void detectDigit(View view) {
        DigitsDetector digitsDetector = new DigitsDetector(this.getApplicationContext());
        Bitmap bm=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        int digit = digitsDetector.detectDigit(bm);
        Log.i("ANSWER", String.valueOf(digit));
        uploadToServer(digit);
        Intent intent = new Intent(CaptureActivity.this, MainActivity.class);
        startActivity(intent);
    }

    // Method to upload file to server
    public void uploadToServer(int digit) {
        String finalUploadUrl = SERVER_URL + "/uploadFile";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        File file = new File(finalFileName);
        RequestParams fileUploadParameters = new RequestParams();
        fileUploadParameters.put("categoryvalue", digit);
        try {
            fileUploadParameters.put("choosefile", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        asyncHttpClient.post(CaptureActivity.this, finalUploadUrl, fileUploadParameters, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast toast = Toast.makeText(getApplicationContext(), "Successfully uploaded" + "  "+ digit, Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast toast = Toast.makeText(getApplicationContext(), "Error while uploading!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}