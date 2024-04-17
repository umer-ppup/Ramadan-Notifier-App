package com.atrule.ramadannotifier.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.atrule.ramadannotifier.R;
import com.atrule.ramadannotifier.adapters.SpinnerAdapter;
import com.atrule.ramadannotifier.receivers.MyReceiver;
import com.atrule.ramadannotifier.shared_preferences.SharedPref;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //region variable declaration
    Spinner spCountry, spFiqah;
    SpinnerAdapter spCountryAdapter, spFiqahAdapter;
    List<String> cities, fiqah;
    String cityString = "", fiqahString = "";
    Button btnGo;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    int cityPosition = 0, fiqahPosition = 0;
    SharedPref sharedPref;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region layout setting
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //endregion

        //region variable initialization
        spCountry = findViewById(R.id.spCountry);
        spFiqah = findViewById(R.id.spFiqah);
        btnGo = findViewById(R.id.buttonGo);
        //endregion

        //region get cities and fiqah string array lists
        cities = Arrays.asList(getResources().getStringArray(R.array.country));
        fiqah = Arrays.asList(getResources().getStringArray(R.array.fiqah));
        //endregion

        //region setting data on city dropdown spinner
        spCountryAdapter = new SpinnerAdapter(cities, MainActivity.this);
        spCountry.setAdapter(spCountryAdapter);
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityPosition = position;
                cityString = cities.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //endregion

        //region setting data on fiqah dropdown spinner
        spFiqahAdapter = new SpinnerAdapter(fiqah, MainActivity.this);
        spFiqah.setAdapter(spFiqahAdapter);
        spFiqah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fiqahPosition = position;
                fiqahString = fiqah.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //endregion

        //region set spinner selection equal to previous value if exists
        SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        if(sharedpreferences.contains("city_spinner_position")){
            spCountry.setSelection(sharedpreferences.getInt("city_spinner_position", 0));
        }
        if(sharedpreferences.contains("fiqah_spinner_position")){
            spFiqah.setSelection(sharedpreferences.getInt("fiqah_spinner_position", 0));
        }
        //endregion

        //region saving selected information on shared preferences and set service to set alarm of sehr and iftar alarms
        btnGo.setOnClickListener(v -> {
            String name = cityString.toLowerCase().trim().replaceAll("\\s+", "_");
            String file_name;
            String fiqah_name;
            if(fiqahString.equals("Hanfi")){
                file_name = name+"_h.json";
                fiqah_name = "Hanfi";
            }
            else {
                file_name = name+"_j.json";
                fiqah_name = "Jafri";
            }

            SharedPreferences sharedPref = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("file_name", file_name);
            editor.putString("city_name", cityString);
            editor.putString("fiqah_name", fiqah_name);
            editor.putInt("city_spinner_position", cityPosition);
            editor.putInt("fiqah_spinner_position", fiqahPosition);
            if(editor.commit()){
                setRepeatingServiceAlarm(MainActivity.this);
            }
        });
        //endregion
    }

    //region repeating alarm service function
    public void setRepeatingServiceAlarm(Context context) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        Intent intent;

//        Calendar currentCalendar = Calendar.getInstance();
//        int hour24hrs = currentCalendar.get(Calendar.HOUR_OF_DAY);
//        int hour12hrs = currentCalendar.get(Calendar.HOUR);
//        int minutes = currentCalendar.get(Calendar.MINUTE);
//        int seconds = currentCalendar.get(Calendar.SECOND);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        intent = new Intent(context, MyReceiver.class);
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(context, 201, intent, PendingIntent.FLAG_UPDATE_CURRENT|Intent.FILL_IN_DATA);

        if (alarmIntent != null && alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }

        assert alarmMgr != null;
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                24*60*60*1000, alarmIntent);

        Intent nextIntent = new Intent(MainActivity.this, RamadanActivity.class);
        startActivity(nextIntent);
        finish();
    }
    //endregion
}