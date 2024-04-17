package com.atrule.ramadannotifier.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.atrule.ramadannotifier.R;
import com.atrule.ramadannotifier.adapters.SpinnerAdapter;
import com.atrule.ramadannotifier.receivers.MyReceiver;
import com.atrule.ramadannotifier.shared_preferences.SharedPref;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.atrule.ramadannotifier.constants.Constants.iftarTimeAlarm;
import static com.atrule.ramadannotifier.constants.Constants.sehrTimeAlarm;

public class AlarmTimeActivity extends AppCompatActivity {

    TextView tvTime;
    Spinner spinnerAlarmTime;
    SpinnerAdapter timeAdapter;
    List<String> selectedTime;
    int selectedTimePosition = 0;
    String selectedTimeString = "", type;
    SharedPref sharedPref;
    int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_time);

        type = getIntent().getStringExtra("type");

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        setTitle(type + "Time Alarm");

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        sharedPref = new SharedPref(getApplicationContext());

        tvTime = findViewById(R.id.tvTime);
        spinnerAlarmTime = findViewById(R.id.alarmTime);
        selectedTime = Arrays.asList(getResources().getStringArray(R.array.time));

        tvTime.setText("Select " + type + " Time");

        //region setting data on city dropdown spinner
        timeAdapter = new SpinnerAdapter(selectedTime, this);
        spinnerAlarmTime.setAdapter(timeAdapter);

        if (type.equals("Sehr"))
        {
            time = sharedPref.getSehrAlarmTime();
        }
        else if (type.equals("Iftar"))
        {
            time = sharedPref.getIftarAlarmTime();
        }

        spinnerAlarmTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimePosition = position;
                selectedTimeString = selectedTime.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //endregion
    }

    // region BackPressed
    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;
    }
    // endregion

    public void setTime(View view) {

        if (type.equals("Sehr"))
        {
            sehrTimeAlarm = Integer.parseInt(selectedTimeString);
            sharedPref.setSehrAlarmTime(Integer.parseInt(selectedTimeString));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.be_notified) + sehrTimeAlarm + " minutes " +  getString(R.string.everyday) + "for Sehr Alarm");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, which) -> {

                dialog.dismiss();
                setRepeatingServiceAlarm(AlarmTimeActivity.this);
                startActivity(new Intent(AlarmTimeActivity.this, RamadanActivity.class));
                finish();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (type.equals("Iftar"))
        {
            iftarTimeAlarm = Integer.parseInt(selectedTimeString);
            sharedPref.setIftarAlarmTime(Integer.parseInt(selectedTimeString));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.be_notified) + iftarTimeAlarm + " minutes " +  getString(R.string.everyday) + "for Iftar Alarm");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, which) -> {

                dialog.dismiss();
                setRepeatingServiceAlarm(AlarmTimeActivity.this);
                startActivity(new Intent(AlarmTimeActivity.this, RamadanActivity.class));
                finish();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //region repeating alarm service function
    public void setRepeatingServiceAlarm(Context context) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        Intent intent;

//        Calendar currentCalendar = Calendar.getInstance();
//        int hour24hrs = currentCalendar.get(Calendar.HOUR_OF_DAY);
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
    }
    //endregion

}