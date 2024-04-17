package com.atrule.ramadannotifier.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //context.stopService(new Intent(context, AlarmService.class));
            setRepeatingServiceAlarm(context);
        }
    }

    //region By default, all alarms are canceled when a device shuts down. To prevent this from happening, you can design your application to automatically restart a repeating alarm if the user reboots the device.
    void setRepeatingServiceAlarm(Context context){
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        Intent intent;

        Calendar currentCalendar = Calendar.getInstance();
        int hour24hrs = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = currentCalendar.get(Calendar.MINUTE);
        int seconds = currentCalendar.get(Calendar.SECOND);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour24hrs);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);

        intent = new Intent(context, MyReceiver.class);
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
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