package com.atrule.ramadannotifier.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.atrule.ramadannotifier.services.AlarmService;

public class AftarReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //region open aftar alarm service when aftar time came
        Intent i = new Intent(context, AlarmService.class);
        //context.stopService(new Intent(context, AlarmService.class));
        i.putExtra("alarm", "Aftar");
        AlarmService.enqueueWork(context, i);
        //endregion
    }
}
