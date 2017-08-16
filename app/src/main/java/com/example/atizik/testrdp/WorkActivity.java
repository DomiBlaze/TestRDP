package com.example.atizik.testrdp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class WorkActivity extends AppCompatActivity {


    public HashMap <ImageView, File> forUpload=new HashMap<ImageView, File>();
    public Uri number;
    private Context mContext;
    public String token;
    public String orig_url_req;
    public String url;
    public HashMap<String, String> statusMap=new HashMap<String, String>();
    public HashMap<String, String> colorMap=new HashMap<String, String>();
    public int cancelStatus = 6;
    public Activity activity;
    public String id_m;
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        mContext = getApplicationContext();
        String fio = (String) bd.get("fio");
        String date = (String) bd.get("date");
        orig_url_req = (String) bd.get("url_req");
        id_m = (String) bd.get("id");
        url = (String) bd.get("url");
        token = (String) bd.get("token");
        TextView fioTV = (TextView) findViewById(R.id.fioTV);
        fioTV.setText(fio);
        activity = this;


        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        statusMap.put("1","Запланировано");
        statusMap.put("2","Выполнено");
        statusMap.put("3","Частично");
        statusMap.put("4","Отменено");
        statusMap.put("5","Без выезда");
        statusMap.put("6","С выездом");
        statusMap.put("7","В пути");
        statusMap.put("8","Возвращение");
        statusMap.put("11","Нет в плане");
        statusMap.put("12","Выполняется");
        statusMap.put("13","Множественный");


        colorMap.put("1","#c0c0c0");
        colorMap.put("2","#6cc468");
        colorMap.put("3","#ff9393");
        colorMap.put("4","#f54b72");
        colorMap.put("5","#f19eb1");
        colorMap.put("6","#ff5e5e");
        colorMap.put("7","#fced65");
        colorMap.put("8","#17ff17");
        colorMap.put("11","#ffffff");
        colorMap.put("12","#ffaa55");
        colorMap.put("13","#27bec2");


        EasyImage.configuration(this).setAllowMultiplePickInGallery(true);

        Resources res = getResources();
        final String[] reasons_part = res.getStringArray(R.array.reasons_part);
        final String[] reasons_bad_after = res.getStringArray(R.array.reasons_bad_after);
        final String[] reasons_bad_before = res.getStringArray(R.array.reasons_bad_before);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder4 = new AlertDialog.Builder(this);

        //контакты
      //  builder4.setTitle("Контакты")
      //          .setItems()
        //контакты;


        //Загрузка
        ((Button) findViewById(R.id.uploadB))
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUpload();
            }
        });
        //Загрузка


        //Бригада
        ((Button) findViewById(R.id.teamB))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Cache.Entry entry = WorkListActivity.requestQueue.getCache().get(orig_url_req);

                        if (entry != null) {

                            try {
                                JSONObject data = new JSONObject(new String(entry.data));
                                populateBrig(data, layout);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        AlertDialog.Builder builder10 = new AlertDialog.Builder(activity);
                        builder10.setView(layout);
                        builder10.show();
                    }
                });

        //Бригада


        //Причина частичного
        builder1.setTitle("Причина частичного выполнения")
                .setItems(R.array.reasons_part, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendNetworkRequest(url + "set_status" + "?token=" + token + "&id=" + id_m + "&status=" + 3 + "&reason=" + reasons_part[which], true);
                    }
                });
        final AlertDialog dialog_pick_reason = builder1.create();
        //Причина частичного;


        //Причина после выезда
        builder2.setTitle("Причина невыполнения после выезда")
                .setItems(R.array.reasons_bad_after, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendNetworkRequest(url + "set_status" + "?token=" + token + "&id=" + id_m + "&status=" + 6 + "&reason=" + reasons_bad_after[which], true);
                    }
                });
        final AlertDialog dialog_pick_reason_after = builder2.create();
        //Причина после выезда;


        //Причина до выезда
        builder3.setTitle("Причина невыполнения до выезда")
                .setItems(R.array.reasons_bad_before, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendNetworkRequest(url + "set_status" + "?token=" + token + "&id=" + id_m + "&status=" + 5 + "&reason=" + reasons_bad_before[which], true);
                    }
                });
        final AlertDialog dialog_pick_reason_before = builder3.create();
        //Причина до выезда;



        //начало монтажа
        builder.setMessage("Начать монтаж?")
                .setTitle("Вы уверены?");

        builder.setPositiveButton("Начать", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                sendNetworkRequest(url + "set_status" + "?token=" + token + "&id=" + id_m + "&status=" + 7, true);

            }
        });
        builder.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });


        final AlertDialog dialog = builder.create();

        ((Button) findViewById(R.id.startB))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.show();
                        Log.d("MyTag", "On click");

                    }
                });
        //начало монтажа;




        //приступить
        ((Button) findViewById(R.id.startSecondB))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendNetworkRequest(url + "set_status" + "?token=" + token + "&id=" + id_m + "&status=" + 12, true);
                    }
                });
        //приступить;


        //отмена монтажа
        ((Button) findViewById(R.id.badB))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (cancelStatus){
                            case 5:
                                dialog_pick_reason_before.show();
                                break;
                            case 6:
                                dialog_pick_reason_after.show();
                                break;
                        }
                        //sendNetworkRequest(url + "set_status" + "?token=" + token + "&id=" + id_m + "&status=" + cancelStatus, true);
                    }
                });
        //отмена монтажа;




        //монтаж выполнен
        builder.setMessage("")
                .setTitle("Статус выполнения");

        builder.setPositiveButton("Полностью", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sendNetworkRequest(url + "set_status" + "?token=" + token + "&id=" + id_m + "&status=" + 2, true);
            }
        });

        builder.setNegativeButton("Частично", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog_pick_reason.show();
            }
        });

        final AlertDialog dialog_end = builder.create();
        ((Button) findViewById(R.id.goodB))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_end.show();
                    }
                });
        //монтаж выполнен



        //EasyImage.openChooserWithGallery(this, "Выберите источник", 0);

        ((Button) findViewById(R.id.contactsB))
        .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });


        ((Button) findViewById(R.id.photosB))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startImagesActivity();
                       // EasyImage.openChooserWithGallery(activity, "Pick source", 0);
                    }
                });





        sendNetworkRequest(orig_url_req, false);


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
                LinearLayout table_img = (LinearLayout) findViewById(R.id.img_table);
                table_img.removeAllViews();
                table_img.setOrientation(LinearLayout.HORIZONTAL);


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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(WorkActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }


    public void populateBrig(final JSONObject response, View dialoglayout) throws JSONException {


        JSONArray jsonArray = response.getJSONArray("brigade");




      //  LayoutInflater inflater = LayoutInflater.from(this);
     //   RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.works_table_row_layout, null, false);

      //  TextView idV = (TextView) layout.findViewById(R.id.idTV);


        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.brigade_layout, null, false);

        layout.findViewById(R.id.br)


        ll.removeAllViews();
        if (jsonArray.length() == 0) {
            //TextView nothing = (TextView) findViewById(R.id.nothing);
            //nothing.setVisibility(View.VISIBLE);
        }
        else {

            for (int i = 0; i < jsonArray.length(); i++) {
                String status, id, addr, brand, zakaz;

                final JSONObject object = jsonArray.getJSONObject(i);
                id = object.getString("id");
                //zakaz = object.getString("zakaz");
               // addr = "Empty";//object.getString("addr");
                //brand = object.getString("brand");
                //status = object.getString("status");




                LayoutInflater inflater = LayoutInflater.from(this);


                TextView fioTV = (TextView) layout.findViewById(R.id.fioTV);




                fioTV.setText(id);


                ll.addView(layout,i);

             /*   TableRow row= new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                TextView nameV = new TextView(this);
                TextView descV = new TextView(this);
                TextView upvotesV = new TextView(this);
                ImageView imgV = new ImageView(this);
                row.addView(nameV);
                row.addView(upvotesV);
                ll.addView(row,j);
                j++;
                TableRow row_1= new TableRow(this);
                row_1.setLayoutParams(lp);
                row_1.addView(descV);
                ll.addView(row_1,j);
                j++;*/

            }
        }
    }

    private void imageUpload() {

        spinner.setVisibility(View.VISIBLE);
        final Button uploadB = (Button) findViewById(R.id.uploadB);
        uploadB.setEnabled(false);
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST,
                url + "report" + "?token=" + token + "&id=" + id_m,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        uploadB.setEnabled(true);
                        spinner.setVisibility(View.GONE);
                            // JSON error

                        Toast.makeText(getApplicationContext(), "Успешно " + response, Toast.LENGTH_LONG).show();
                        LinearLayout table_img = (LinearLayout) findViewById(R.id.img_table);
                        table_img.removeAllViews();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                spinner.setVisibility(View.INVISIBLE);
                uploadB.setEnabled(true);

            }
        });

        int i=0;
        for(File imageF: forUpload.values()) {
            smr.addFile("image_" + i++, imageF.getPath());
        }

        MyApplication.getInstance().addToRequestQueue(smr);

    }



    public void sendNetworkRequest(final String url_req, final boolean send) {

        //TextView nothing = (TextView) findViewById(R.id.nothing);
        //nothing.setVisibility(View.INVISIBLE);


        spinner.setVisibility(View.VISIBLE);


        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url_req,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MyTag", "Response: " + response.toString());
                        try {
                            spinner.setVisibility(View.GONE);
                            if(!send)
                                populateData(response);
                            else
                                sendNetworkRequest(orig_url_req, false);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Cache.Entry entry = WorkListActivity.requestQueue.getCache().get(url_req);

                    if (entry != null) {

                        try {
                            JSONObject data = new JSONObject(new String(entry.data));
                            populateData(data);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        // process data
                    }


                }

                Toast toast = Toast.makeText(getApplicationContext(), "Ошибка сети, попробуйте позже", Toast.LENGTH_LONG);
                toast.show();
                spinner.setVisibility(View.INVISIBLE);
            }
        });
        WorkListActivity.requestQueue.getCache().invalidate(url_req,true);
        WorkListActivity.requestQueue.add(jsonObjectRequest);
    }
    public void populateData(JSONObject response) throws JSONException {


        JSONObject object = response.getJSONObject("content");







           // for (int i = 0; i < jsonArray.length(); i++) {
        String status, zakaz, date, brand, work, addr;

                //JSONObject object = jsonArray.getJSONObject(i);
        addr = object.getString("addr");
        zakaz = object.getString("zakaz");
        status = object.getString("status");
        date = object.getString("date");
        brand = object.getString("brand");
        work = object.getString("work");
        number = Uri.parse("tel:" + object.getString("clients_contact"));



       // TextView idTV = (TextView) findViewById(R.id.idTV);
        TextView addrTV = (TextView) findViewById(R.id.addrTV);
        TextView dateTV = (TextView) findViewById(R.id.dateTV);
        TextView brandTV = (TextView) findViewById(R.id.brandTV);
        TextView workTV = (TextView) findViewById(R.id.workTV);

       // idTV.setText(zakaz);
        dateTV.setText(date);
        brandTV.setText(brand);
        workTV.setText("S-" + zakaz + "/r-" + work);
        addrTV.setText(addr);

        Button goodB = (Button) findViewById(R.id.goodB);
        Button badB = (Button) findViewById(R.id.badB);
        Button startB = (Button) findViewById(R.id.startB);
        Button startSecondB = (Button) findViewById(R.id.startSecondB);
        Button photosB = (Button) findViewById(R.id.photosB);
        Button selfB = (Button) findViewById(R.id.selfB);


        if (status == "1") {
            photosB.setEnabled(false);
            selfB.setEnabled(false);

        }
        else {
            photosB.setEnabled(true);
            selfB.setEnabled(true);
        }

        goodB.setVisibility(View.INVISIBLE);
        badB.setVisibility(View.INVISIBLE);
        startB.setVisibility(View.INVISIBLE);
        startSecondB.setVisibility(View.INVISIBLE);

        cancelStatus = 6;
        switch (status){
            case "1":
                startB.setVisibility(View.VISIBLE);
                badB.setVisibility(View.VISIBLE);
                cancelStatus = 5;
                break;

            case "7":
                startSecondB.setVisibility(View.VISIBLE);
                badB.setVisibility(View.VISIBLE);
                ;
                break;
            case "12":
                goodB.setVisibility(View.VISIBLE);
                badB.setVisibility(View.VISIBLE);
                break;



        }


        }
    public void startImagesActivity() {
        Intent intent = new Intent(this, ImagesActivity.class);
        Bundle extras = new Bundle();

        extras.putString("token",token);
        extras.putString("id_m",id_m);
        extras.putString("url",url);

        intent.putExtras(extras);

        startActivity(intent);

    }
    }

