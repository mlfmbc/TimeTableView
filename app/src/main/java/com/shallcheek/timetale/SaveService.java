package com.shallcheek.timetale;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by chang on 2017/4/27.
 */

public class SaveService extends IntentService {
    private static final String ACTION_SAVE = "com.shallcheek.timetale.action.SAVE";
    private ScrollView ScrollView;
    public SaveService(String name,ScrollView ScrollView) {
        super(name);
        this.ScrollView=ScrollView;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SaveUtil.SaveBitmapByView(this,ScrollView,"MEC",0);
    }
    public static void startSave(Context context, String path,ScrollView v)
    {
        Intent intent = new Intent(context, SaveService.class);
        intent.setAction(ACTION_SAVE);
        context.startService(intent);
    }
}
