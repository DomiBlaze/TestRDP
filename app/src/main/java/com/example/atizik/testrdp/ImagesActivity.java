package com.example.atizik.testrdp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


//import com.android.volley.VolleyError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ImagesActivity extends AppCompatActivity {

    public HashMap<ImageView, File> forUpload=new HashMap<ImageView, File>();
    public Activity activity;
    public String url;
    public String token;
    public String id_m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        id_m = (String) bd.get("id_m");
        url = (String) bd.get("url");
        token = (String) bd.get("token");


        ((Button) findViewById(R.id.addB))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EasyImage.openChooserWithGallery(activity, "Pick source", 0);
                    }
                });

        ((Button) findViewById(R.id.uploadB))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        imageUpload();

                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                //onPhotosReturned(imageFiles);
                GridLayout table_img = (GridLayout) findViewById(R.id.grid_images);

                table_img.removeAllViewsInLayout();



                LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                imgViewParams.setMargins(10,10,10,10);




                for (File imageF: imageFiles){

                    final ImageView imgV = new ImageView(activity);
                    Picasso.with(activity).load(imageF).resize(250,250).centerCrop().into(imgV);
                    forUpload.put(imgV,imageF);
                    imgV.setLayoutParams(imgViewParams);
                    imgV.setClickable(true);
                    imgV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            forUpload.remove(imgV);
                            ((ViewGroup)imgV.getParent()).removeView(imgV);
                        }
                    });
                    table_img.addView(imgV);

                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(ImagesActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }


    private void imageUpload() {

       // spinner.setVisibility(View.VISIBLE);
        final Button addB = (Button) findViewById(R.id.addB);
        final Button uploadB = (Button) findViewById(R.id.uploadB);
        addB.setEnabled(false);
        uploadB.setEnabled(false);

        Log.d("ID:", id_m);
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST,
                url + "report" + "?token=" + token + "&id=" + id_m,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        addB.setEnabled(true);
                        uploadB.setEnabled(true);
                       // spinner.setVisibility(View.GONE);
                        // JSON error

                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        GridLayout table_img = (GridLayout) findViewById(R.id.grid_images);
                        table_img.removeAllViewsInLayout();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
               // spinner.setVisibility(View.INVISIBLE);
                addB.setEnabled(true);
                uploadB.setEnabled(true);

            }
        });

        int i=0;
        for(File imageF: forUpload.values()) {
            smr.addFile("image_" + i++, imageF.getPath());
        }


        MyApplication.getInstance().addToRequestQueue(smr);

    }
}
