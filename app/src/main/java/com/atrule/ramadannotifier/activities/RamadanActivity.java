package com.atrule.ramadannotifier.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.atrule.ramadannotifier.R;
import com.atrule.ramadannotifier.classes.GetJsonString;
import com.atrule.ramadannotifier.classes.Ramadan;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RamadanActivity extends AppCompatActivity {
    //region variable declaration
    int n = 1;
    TextView tvSehriTime, tvAftariTime, tvRamadan, tvRamadanDate, tvCityName, tvDay, tvHour, tvMinute,
            tvSecond, tvRemaining, tvRemainingA, tvRamadanStartDate, tvIftarDay, tvIftarHour,
            tvIftarMinute, tvIftarSecond;
    final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.getDefault());
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    String file_name = "", city_name = "", fiqah_name = "";
    LinearLayout linearLayout, linearLayoutAftar;
    ConstraintLayout clickLayout;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region setting layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ramadan);
        //endregion

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.app_name));

        //region getting and setting saved city, fiqah and calendar file from shared preference
        SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        file_name = sharedpreferences.getString("file_name", "");
        city_name = sharedpreferences.getString("city_name", "");
        fiqah_name = sharedpreferences.getString("fiqah_name", "");
        //endregion

        //region variable initialization
        tvSehriTime = findViewById(R.id.tvSehriTime);
        tvAftariTime = findViewById(R.id.tvAftariTime);
        tvRamadan = findViewById(R.id.tvRamadan);
        tvRamadanDate = findViewById(R.id.tvRamadanDate);
        tvCityName = findViewById(R.id.tvCityName);
        tvDay = findViewById(R.id.tvDay);
        tvHour = findViewById(R.id.tvHour);
        tvMinute = findViewById(R.id.tvMinute);
        tvSecond = findViewById(R.id.tvSecond);
        tvIftarDay = findViewById(R.id.tvDayAftar);
        tvIftarHour = findViewById(R.id.tvHourAftar);
        tvIftarMinute = findViewById(R.id.tvMinuteAftar);
        tvIftarSecond = findViewById(R.id.tvSecondAftar);
        linearLayout = findViewById(R.id.linearLayout);
        tvRemaining = findViewById(R.id.tvRemaining);
        tvRemainingA = findViewById(R.id.tvRemainingA);
        linearLayoutAftar = findViewById(R.id.linearLayoutAftar);
        tvRamadanStartDate = findViewById(R.id.tvRamadanStartDate);
        clickLayout = findViewById(R.id.clickLayout);
        //endregion

        //region click function to change city or fiqah
        clickLayout.setOnClickListener(v -> {
            Intent nextIntent = new Intent(RamadanActivity.this, MainActivity.class);
            startActivity(nextIntent);
        });
        //endregion

        //region setting ramadan information
        String json = GetJsonString.getJsonFromAssets(RamadanActivity.this, file_name);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String dateaString = dateFormat.format(date);

        //region making a ramadan calendar object based on the current date and time
        Ramadan ramadan = new Ramadan();
        try {
            assert json != null;
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(dateaString)) {
                JSONObject jsonObject1 = jsonObject.getJSONObject(dateaString);
                ramadan.setDate(dateaString);
                ramadan.setRamadan(jsonObject1.getString("RAMADAN"));
                ramadan.setSehriTime(jsonObject1.getString("SEHRI"));
                ramadan.setAftarTime(jsonObject1.getString("IFTAR"));
            } else {
                JSONObject jsonObject1 = jsonObject.getJSONObject("14 April 2021");
                ramadan.setDate("14 April 2021");
                ramadan.setRamadan("");
                ramadan.setSehriTime(jsonObject1.getString("SEHRI"));
                ramadan.setAftarTime(jsonObject1.getString("IFTAR"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //endregion

        //region setting values to the layout according to the above ramadan calendar object
        tvSehriTime.setText(ramadan.getSehriTime());
        tvAftariTime.setText(ramadan.getAftarTime());
        tvRamadanDate.setText(dateaString);
        String ramdanDate = getRamdanDate(ramadan.getRamadan());

        tvRamadan.setText(ramdanDate);
        if(ramadan.getRamadan().equals("")){
            tvRamadan.setVisibility(View.GONE);
            tvRamadanStartDate.setText(R.string.starting_ramadan_date);
            tvRamadanStartDate.setVisibility(View.VISIBLE);
        }
        else{
            tvRamadan.setVisibility(View.VISIBLE);
            tvRamadanStartDate.setVisibility(View.GONE);
        }
        tvCityName.setText(city_name + " (" + fiqah_name + ")");
        //endregion

        //region making date object that will be used in countdown
        String sehriTimeString = ramadan.getDate()+" "+ramadan.getSehriTime();
        String aftarTimeString = ramadan.getDate()+" "+ramadan.getAftarTime();

        try {
            Date sehriDate = dateTimeFormat.parse(sehriTimeString);
            Date aftariDate = dateTimeFormat.parse(aftarTimeString);
            Calendar calendar1 = Calendar.getInstance();
            Date now = calendar1.getTime();

            //region sehr time count down
            CountDownTimer sehriStart = new CountDownTimer(  sehriDate.getTime() - now.getTime() , 1000) {

                public void onTick(long millisUntilFinished) {
                    long day = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.DAYS.toMillis(day);

                    long hour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.HOURS.toMillis(hour);

                    long minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.MINUTES.toMillis(minute);

                    long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                    linearLayout.setVisibility(View.VISIBLE);
                    tvRemaining.setText(getResources().getString(R.string.remaining_time));

                    tvDay.setText("" + day);
                    tvHour.setText("" + hour);
                    tvMinute.setText("" + minute);
                    tvSecond.setText("" + second);
                }

                public void onFinish() {

                    linearLayout.setVisibility(View.GONE);
                    tvRemaining.setText(getString(R.string.sehr_time_of) + ramdanDate + " " + getString(R.string.ramdan_kareem_passed));
                }
            };
            sehriStart.start();
            //endregion

            //region iftar time count down
            CountDownTimer aftarStart = new CountDownTimer(  aftariDate.getTime() - now.getTime() , 1000) {

                public void onTick(long millisUntilFinished) {
                    long day = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.DAYS.toMillis(day);

                    long hour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.HOURS.toMillis(hour);

                    long minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.MINUTES.toMillis(minute);

                    long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);


                    linearLayoutAftar.setVisibility(View.VISIBLE);
                    tvRemainingA.setText(getResources().getString(R.string.remaining_time));

                    tvIftarDay.setText("" + day);
                    tvIftarHour.setText("" + hour);
                    tvIftarMinute.setText("" + minute);
                    tvIftarSecond.setText("" + second);
                }

                public void onFinish() {

                    linearLayoutAftar.setVisibility(View.GONE);
                    tvRemainingA.setText(getString(R.string.iftar_time_of) + ramdanDate + " " + getString(R.string.ramdan_kareem_passed));
                    setForNextRamadan();
                }
            };
            aftarStart.start();
            //endregion
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //endregion

        //endregion
    }

    private String getRamdanDate(String ramadan) {

        String ramdanDate;
        switch (ramadan) {
            case "1":
                ramdanDate = ramadan + "st";
                break;
            case "2":
                ramdanDate = ramadan + "nd";
                break;
            case "3":
                ramdanDate = ramadan + "rd";
                break;
            default:
                ramdanDate = ramadan + "th";
                break;
        }

        return ramdanDate;
    }

    /* Inflating Menu and its Items */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    /* Handling Clicks of Items in Menu */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {

            case R.id.menuSehrTime:
                Intent intent = new Intent(this, AlarmTimeActivity.class);
                intent.putExtra("type", "Sehr");
                startActivity(intent);
                break;

            case R.id.menuIftarTime:
                Intent intent1 = new Intent(this, AlarmTimeActivity.class);
                intent1.putExtra("type", "Iftar");
                startActivity(intent1);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //region provide real time functionality on UI
        if(n == 1){
            n = n + 1;
        }
        else{
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
        //endregion
    }

    //region setting ramadan information function
    void setForNextRamadan(){
        String json = GetJsonString.getJsonFromAssets(RamadanActivity.this, file_name);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date date = calendar.getTime();
        String dateaString = dateFormat.format(date);

        //region making a ramadan calendar object based on the current date and time
        Ramadan ramadan = new Ramadan();
        try {
            assert json != null;
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(dateaString)) {
                JSONObject jsonObject1 = jsonObject.getJSONObject(dateaString);
                ramadan.setDate(dateaString);
                ramadan.setRamadan(jsonObject1.getString("RAMADAN"));
                ramadan.setSehriTime(jsonObject1.getString("SEHRI"));
                ramadan.setAftarTime(jsonObject1.getString("IFTAR"));
            } else {
                JSONObject jsonObject1 = jsonObject.getJSONObject("14 April 2021");
                ramadan.setDate("14 April 2021");
                ramadan.setRamadan("");
                ramadan.setSehriTime(jsonObject1.getString("SEHRI"));
                ramadan.setAftarTime(jsonObject1.getString("IFTAR"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //endregion

        //region setting values to the layout according to the above ramadan calendar object
        tvSehriTime.setText(ramadan.getSehriTime());
        tvAftariTime.setText(ramadan.getAftarTime());
        tvRamadanDate.setText(ramadan.getDate());
        tvRamadanDate.setText(dateaString);
        String ramdanDate = getRamdanDate(ramadan.getRamadan());
        tvRamadan.setText(ramdanDate);
        if(ramadan.getRamadan().equals("")){
            tvRamadan.setVisibility(View.GONE);
            tvRamadanStartDate.setText("Starting from (14 April 2021)");
            tvRamadanStartDate.setVisibility(View.VISIBLE);
        }
        else{
            tvRamadan.setVisibility(View.VISIBLE);
            tvRamadanStartDate.setVisibility(View.GONE);
        }
        tvCityName.setText(city_name+" ("+fiqah_name+")");
        //endregion

        //region making date object that will be used in countdown
        String sehriTimeString = ramadan.getDate()+" "+ramadan.getSehriTime();
        String aftarTimeString = ramadan.getDate()+" "+ramadan.getAftarTime();

        try {
            Date sehriDate = dateTimeFormat.parse(sehriTimeString);
            Date aftariDate = dateTimeFormat.parse(aftarTimeString);
            Calendar calendar1 = Calendar.getInstance();
            Date now = calendar1.getTime();

            //region sehr countdown
            CountDownTimer sehri = new CountDownTimer(  sehriDate.getTime() - now.getTime() , 1000) {

                public void onTick(long millisUntilFinished) {
                    long day = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.DAYS.toMillis(day);

                    long hour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.HOURS.toMillis(hour);

                    long minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.MINUTES.toMillis(minute);

                    long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                    linearLayout.setVisibility(View.VISIBLE);
                    tvRemaining.setText("Remaining Time");

                    tvDay.setText("" + day);
                    tvHour.setText("" + hour);
                    tvMinute.setText("" + minute);
                    tvSecond.setText("" + second);
                }

                public void onFinish() {
                    tvDay.setText("0");
                    tvHour.setText("0");
                    tvMinute.setText("0");
                    tvSecond.setText("0");
                    linearLayout.setVisibility(View.GONE);
                    tvRemaining.setText("Sehr time of " + ramdanDate + " Ramadan Kareem has passed.");
                }
            }.start();
            //endregion

            //region iftar count down
            CountDownTimer aftar = new CountDownTimer(  aftariDate.getTime() - now.getTime() , 1000) {

                public void onTick(long millisUntilFinished) {
                    long day = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.DAYS.toMillis(day);

                    long hour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.HOURS.toMillis(hour);

                    long minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    millisUntilFinished -= TimeUnit.MINUTES.toMillis(minute);

                    long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                    linearLayoutAftar.setVisibility(View.VISIBLE);
                    tvRemainingA.setText("Remaining Time");
                    tvIftarDay.setText("" + day);
                    tvIftarHour.setText("" + hour);
                    tvIftarMinute.setText("" + minute);
                    tvIftarSecond.setText("" + second);
                }

                public void onFinish() {
                    tvIftarDay.setText("0");
                    tvIftarHour.setText("0");
                    tvIftarMinute.setText("0");
                    tvIftarSecond.setText("0");
                    linearLayoutAftar.setVisibility(View.GONE);
                    tvRemainingA.setText("Iftar time of " + ramdanDate + " Ramadan Kareem has passed.");
                }
            }.start();
            //endregion
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //endregion
    }
    //endregion
}