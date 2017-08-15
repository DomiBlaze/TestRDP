package com.example.atizik.testrdp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class WorkListActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public HashMap<String, String> statusMap=new HashMap<String, String>();
    public HashMap<String, String> colorMap=new HashMap<String, String>();
    private Context mContext;
    public static RequestQueue requestQueue;
    public Calendar calendar = Calendar.getInstance();

    public String token, fio_txt;
    public String url = "https://dev.rdpgroup.ru/api/montage/";
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_list);


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


        Button dateV = (Button) findViewById(R.id.button_date);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        mContext = getApplicationContext();

        token = (String) bd.get("token");

        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);
        TextView fio =(TextView) findViewById(R.id.fioTV);
        fio_txt = (String) bd.get("fio");
        fio.setText(fio_txt);

        String myFormat = "dd.MM.yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        dateV.setText(sdf.format(calendar.getTime()));
        dateV.bringToFront();


        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, WorkListActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(calendar.getTimeInMillis() - 1000*60*60*24);
        datePicker.setMaxDate(calendar.getTimeInMillis() + (2*1000*60*60*24));
        sendNetworkRequest(UrlForRequest("list","",sdf.format(calendar.getTime())),true);


        ((Button) findViewById(R.id.button_date))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePickerDialog.show();
                    }
                });



    }

    public String UrlForRequest(String request, String id, String date) {
        String request_url = "no request";
        switch (request) {
            case "list": request_url = url + "list" + "?token=" + token + "&date=" + date;
                break;
            case "montageinfo": request_url = url + "montageinfo" + "?token=" + token + "&id=" + id;
                break;
        }

        return request_url;
    }
    public void populateTable(final JSONObject response) throws JSONException {


        JSONArray jsonArray = response.getJSONArray("content");


        TableLayout ll = (TableLayout) findViewById(R.id.works);
        ll.removeAllViews();
        if (jsonArray.length() == 0) {
             TextView nothing = (TextView) findViewById(R.id.nothing);
             nothing.setVisibility(View.VISIBLE);
        }
        else {

            for (int i = 0; i < jsonArray.length(); i++) {
                String status, id, addr, brand, zakaz;

                final JSONObject object = jsonArray.getJSONObject(i);
                id = object.getString("id");
                zakaz = object.getString("zakaz");
                addr = "Empty";//object.getString("addr");
                brand = object.getString("brand");
                status = object.getString("status");

                sendNetworkRequest(UrlForRequest("montageinfo",id,""),false);


                LayoutInflater inflater = LayoutInflater.from(this);
                RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.works_table_row_layout, null, false);

                TextView idV = (TextView) layout.findViewById(R.id.idTV);
                TextView statusV = (TextView) layout.findViewById(R.id.status);
                TextView brandV = (TextView) layout.findViewById(R.id.brandTV);
                TextView addrV = (TextView) layout.findViewById(R.id.addrTV);

                TextView background = ((TextView) layout.findViewById(R.id.background));
                background.setClickable(true);
                background.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    startWorkActivity(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                layout.setBackgroundColor(Color.parseColor(colorMap.get(status)));



                idV.setText(zakaz);
                statusV.setText(statusMap.get(status));
                brandV.setText(brand);
                addrV.setText(addr);


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



    public void startWorkActivity(JSONObject response) throws JSONException {
        Intent intent = new Intent(this, WorkActivity.class);
        Bundle extras = new Bundle();

        extras.putString("token",token);

        extras.putString("fio",fio_txt);
        extras.putString("id",response.getString("id"));
        extras.putString("url_req",url + "montageinfo" + "?token=" + token + "&id=" + response.getString("id"));
        extras.putString("url",url);

        intent.putExtras(extras);

        startActivity(intent);

    }



    public void sendNetworkRequest(final String url_req, final boolean popul) {

        TextView nothing = (TextView) findViewById(R.id.nothing);
        nothing.setVisibility(View.INVISIBLE);
        requestQueue = Volley.newRequestQueue(mContext);

        spinner.setVisibility(View.VISIBLE);
        // mSwipeRefreshLayout.setRefreshing(true);

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
                            if (popul)
                                populateTable(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Cache.Entry entry = requestQueue.getCache().get(url_req);

                    if(entry!=null){

                            try {
                                JSONObject data = new JSONObject(String.valueOf(entry.data));
                                if (popul)
                                    populateTable(data);
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




        requestQueue.add(jsonObjectRequest);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String myFormat = "dd.MM.yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        Button dateV = (Button) findViewById(R.id.button_date);
        dateV.setText(sdf.format(calendar.getTime()));
        sendNetworkRequest(UrlForRequest("list","",sdf.format(calendar.getTime())),true);
    }

    @Override
    protected void onRestart() {
// TODO Auto-generated method stub
        super.onRestart();
        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        sendNetworkRequest(UrlForRequest("list","",sdf.format(calendar.getTime())),true);
        //Do your code here
    }


}
