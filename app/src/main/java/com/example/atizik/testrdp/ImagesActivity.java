package com.example.atizik.testrdp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


//import com.android.volley.VolleyError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.VideoPicker;
import com.kbeanie.multipicker.api.callbacks.VideoPickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kbeanie.multipicker.api.entity.ChosenVideo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


public class ImagesActivity extends AppCompatActivity {

    public HashMap<ImageView, File> forUpload=new HashMap<ImageView, File>();
    public Activity activity;
    public String url;
    public String token;
    public String id_m;
    public String url_work_request;
    public Uri video_uri;
    public String report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        String brand = (String) bd.get("brand");
        String zakaz_work = (String) bd.get("zakaz_work");
        TextView brandTV = (TextView) findViewById(R.id.brandTV);
        brandTV.setText(brand);
        TextView zakaz_workTV = (TextView) findViewById(R.id.zakaz_workTV);
        zakaz_workTV.setText(zakaz_work);
        id_m = (String) bd.get("id_m");
        url = (String) bd.get("url");
        token = (String) bd.get("token");
        report = (String) bd.get("report_type");
        url_work_request = (String) bd.get("url_work_request");
        String[] img_urls = (String[]) bd.get("img_urls");








        ((Button) findViewById(R.id.videoB))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        VideoPicker videoPicker = new VideoPicker(activity);
                        videoPicker.setVideoPickerCallback(new VideoPickerCallback(){
                                                               @Override
                                                               public void onVideosChosen(List<ChosenVideo> videos ) {
                                                                   // Display images
                                                               }

                                                               @Override
                                                               public void onError(String message) {
                                                                   // Do error handling
                                                               }
                                                           }
                        );

                        //videoPicker.pickVideo();






                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK );
                        photoPickerIntent.setType("video/*");
                        //startActivity(Intent.createChooser(photoPickerIntent,"Видео"));

                        startActivityForResult(photoPickerIntent, 33451);

                    }
                });

        showUploaded(img_urls);
        ((Button) findViewById(R.id.addB))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EasyImage.openChooserWithGallery(activity, "Pick source", 0);
                    }
                });

        ((Button) findViewById(R.id.uploadB))
                .setEnabled(false);
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

      //  if(resultCode == 33452) {
      //      if(requestCode == Picker.PICK_IMAGE_DEVICE) {
      //          if(videoPicker == null) {
       //             videoPicker = new VideoPicker(activity);
       //             videoPicker.setVideoPickerCallback(videoPickerCallback);
       //         }
       //         videoPicker.submit(data);
        //    }
       // }

        if(requestCode == 33451 && resultCode == Activity.RESULT_OK)
            {

                    video_uri = data.getData();
                    ((Button) findViewById(R.id.uploadB)).setEnabled(true);




            }
        else {

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
                    imgViewParams.setMargins(10, 10, 10, 10);


                    for (File imageF : imageFiles) {

                        ((Button) findViewById(R.id.uploadB)).setEnabled(true);
                        final ImageView imgV = new ImageView(activity);

                        File compressedImageFile = null;
                        try {
                            compressedImageFile = new Compressor(activity).compressToFile(imageF);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Picasso.with(activity).load(compressedImageFile).resize(250, 250).centerCrop().into(imgV);

                        forUpload.put(imgV, compressedImageFile);
                        imgV.setLayoutParams(imgViewParams);
                        imgV.setClickable(true);
                        imgV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                forUpload.remove(imgV);
                                ((ViewGroup) imgV.getParent()).removeView(imgV);
                                if(forUpload.isEmpty() && video_uri == null)
                                    ((Button) findViewById(R.id.uploadB)).setEnabled(false);
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
    }




    private void showUploaded(String[] urls) {
        //Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        GridLayout table_img = (GridLayout) findViewById(R.id.grid_uploaded_images);
        table_img.removeAllViewsInLayout();

        LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgViewParams.setMargins(10,10,10,10);


        for (String imageF: urls){

            final ImageView imgV = new ImageView(activity);
            Picasso.with(activity).load(imageF).resize(250,250).centerCrop().into(imgV);

            imgV.setLayoutParams(imgViewParams);
            table_img.addView(imgV);

        }


    }



    private void videoUpload(){



    }



    private void imageUpload() {

       // spinner.setVisibility(View.VISIBLE);
        final Button addB = (Button) findViewById(R.id.addB);
        final Button uploadB = (Button) findViewById(R.id.uploadB);
        final Button videoB = (Button) findViewById(R.id.videoB);
        addB.setEnabled(false);
        uploadB.setEnabled(false);
        videoB.setEnabled(false);


        Log.d("ID:", id_m);
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST,
                url + report + "?token=" + token + "&id=" + id_m,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        addB.setEnabled(true);
                        uploadB.setEnabled(true);
                        videoB.setEnabled(true);
                       // spinner.setVisibility(View.GONE);
                        // JSON error

                        Toast.makeText(getApplicationContext(), "Файлы загружены", Toast.LENGTH_LONG).show();
                        GridLayout table_img = (GridLayout) findViewById(R.id.grid_images);
                        table_img.removeAllViewsInLayout();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Ошибка сети, попробуйте позже", Toast.LENGTH_LONG).show();
               // spinner.setVisibility(View.INVISIBLE);
                addB.setEnabled(true);
                uploadB.setEnabled(true);
                videoB.setEnabled(true);

            }
        });

        int i=0;
        for(File imageF: forUpload.values()) {
            smr.addFile("image_" + i++, imageF.getPath());

        }



        if (video_uri!=null) {
        String filePath = null;

        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getApplicationContext().getContentResolver().query(video_uri,
                filePathColumn, null, null, null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                filePath = cursor.getString(columnIndex);
            } finally {
                cursor.close();
            }
        }


            File file = new File(filePath);
            if (file.length()<15*1024*1024)
                smr.addFile("video_", filePath);
            else
                Toast.makeText(getApplicationContext(), "Слишком большой видеофайл размером " + file.length()/1024/1024 + "мб, максимальный размер 15мб", Toast.LENGTH_LONG).show();
        }
        video_uri = null;

        //+
        // smr.setRetryPolicy(new DefaultRetryPolicy(5000,5,2));
        MyApplication.getInstance().addToRequestQueue(smr);
        forUpload.clear();
        ((Button) findViewById(R.id.uploadB)).setEnabled(false);

    }
}
