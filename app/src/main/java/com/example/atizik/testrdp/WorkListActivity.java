package com.example.atizik.testrdp;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WorkListActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public HashMap<String, String> statusMap=new HashMap<String, String>();
    public HashMap<String, String> colorMap=new HashMap<String, String>();
    public Calendar calendar = Calendar.getInstance();
    private Context mContext;
    public String token;
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
        fio.setText((String) bd.get("fio"));

        String myFormat = "dd.MM.yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        dateV.setText("20.07.2017");//sdf.format(calendar.getTime()));
        dateV.bringToFront();


        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, WorkListActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(calendar.getTimeInMillis());
        sendNetworkRequest("20.07.2017");//sdf.format(calendar.getTime()));


        ((Button) findViewById(R.id.button_date))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePickerDialog.show();
                    }
                });



    }

    public void populateTable(JSONObject response) throws JSONException {


        JSONArray jsonArray = response.getJSONArray("content");


        TableLayout ll = (TableLayout) findViewById(R.id.works);
        ll.removeAllViews();
        if (jsonArray.length() == 0) {
             TextView nothing = (TextView) findViewById(R.id.nothing);
             nothing.setVisibility(View.VISIBLE);
        }
        else {

            for (int i = 0; i < jsonArray.length(); i++) {
                String status, id;

                JSONObject object = jsonArray.getJSONObject(i);
                id = object.getString("id");
                status = object.getString("status");


                LayoutInflater inflater = LayoutInflater.from(this);
                RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.works_table_row_layout, null, false);

                TextView idV = (TextView) layout.findViewById(R.id.id);
                TextView statusV = (TextView) layout.findViewById(R.id.status);
                layout.setBackgroundColor(Color.parseColor(colorMap.get(status)));



                idV.setText(id);
                statusV.setText(statusMap.get(status));


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




    public void sendNetworkRequest(String date) {

        TextView nothing = (TextView) findViewById(R.id.nothing);
        nothing.setVisibility(View.INVISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        spinner.setVisibility(View.VISIBLE);
        // mSwipeRefreshLayout.setRefreshing(true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://dev.rdpgroup.ru/api/montage/list?token=" + token + "&date=" + date,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MyTag", "Response: " + response.toString());
                        try {
                            spinner.setVisibility(View.GONE);
                            populateTable(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
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
        sendNetworkRequest(sdf.format(calendar.getTime()));
    }
}
