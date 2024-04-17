package com.atrule.ramadannotifier.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.atrule.ramadannotifier.classes.GetJsonString;
import com.atrule.ramadannotifier.classes.Ramadan;
import com.atrule.ramadannotifier.shared_preferences.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyReceiver extends BroadcastReceiver {

    //region formatting date patterns
    final SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.getDefault());
    SharedPref sharedPref;
    //endregion

    //region constructor
    public MyReceiver() {
    }
    //endregion

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPref = new SharedPref(context);
        //region set alarm of sehr and iftar on every 01:00 AM
        SharedPreferences sharedpreferences = context.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        if(sharedpreferences.contains("file_name")){
            String file_name = sharedpreferences.getString("file_name", "");

            String json = GetJsonString.getJsonFromAssets(context, file_name);
            Date date = new Date();
            String dateaString = sdfDate.format(date);
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

                    String sehriTimeString = ramadan.getDate()+" "+ramadan.getSehriTime();
                    String aftarTimeString = ramadan.getDate()+" "+ramadan.getAftarTime();

                    try {
                        Date sehriDate = sdf.parse(sehriTimeString);
                        Date aftariDate = sdf.parse(aftarTimeString);

                        Calendar sehriCalendar = Calendar.getInstance();
                        assert sehriDate != null;
                        sehriCalendar.setTime(sehriDate);
                        Calendar aftarCalendar = Calendar.getInstance();
                        assert aftariDate != null;
                        aftarCalendar.setTime(aftariDate);

                        sehriCalendar.set(Calendar.SECOND, 0);
                        aftarCalendar.set(Calendar.SECOND, 0);

                        Calendar currentCallendar = Calendar.getInstance();

                        //region set sehr alarm if it is not passed
                        if(sehriCalendar.after(currentCallendar)){

                            int sehrTime = sharedPref.getSehrAlarmTime();
                            sehriCalendar.add(Calendar.MINUTE, -sehrTime);

                            AlarmManager alarmMgrS;
                            PendingIntent alarmIntentS;
                            Intent intentNewS;

                            intentNewS = new Intent(context, SehriReceiver.class);
                            alarmMgrS = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            alarmIntentS = PendingIntent.getBroadcast(context, 201, intentNewS, PendingIntent.FLAG_UPDATE_CURRENT|Intent.FILL_IN_DATA);

                            if (alarmIntentS != null && alarmMgrS != null) {
                                alarmMgrS.cancel(alarmIntentS);
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                assert alarmMgrS != null;
                                alarmMgrS.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, sehriCalendar.getTimeInMillis(), alarmIntentS);
                            } else {
                                assert alarmMgrS != null;
                                alarmMgrS.setExact(AlarmManager.RTC_WAKEUP, sehriCalendar.getTimeInMillis(), alarmIntentS);
                            }
                        }
                        //endregion

                        //region set iftar time if it is not passed
                        if(aftarCalendar.after(currentCallendar)){

                            int iftarTime = sharedPref.getIftarAlarmTime();
                            aftarCalendar.add(Calendar.MINUTE, -iftarTime);

                            AlarmManager alarmMgrA;
                            PendingIntent alarmIntentA;
                            Intent intentNewA;

                            intentNewA = new Intent(context, AftarReceiver.class);
                            alarmMgrA = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            alarmIntentA = PendingIntent.getBroadcast(context, 201, intentNewA, PendingIntent.FLAG_UPDATE_CURRENT|Intent.FILL_IN_DATA);

                            if (alarmIntentA != null && alarmMgrA != null) {
                                alarmMgrA.cancel(alarmIntentA);
                            }

                            assert alarmMgrA != null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarmMgrA.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, aftarCalendar.getTimeInMillis(), alarmIntentA);
                            } else {
                                alarmMgrA.setExact(AlarmManager.RTC_WAKEUP, aftarCalendar.getTimeInMillis(), alarmIntentA);
                            }
                        }
                        //endregion

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //endregion
    }
}