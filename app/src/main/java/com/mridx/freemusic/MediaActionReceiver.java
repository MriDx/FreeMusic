package com.mridx.freemusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        assert action != null;
        if(action.equalsIgnoreCase("com.mridx.freemusic.ACTION_PLAY")){
            // do your stuff to play action;

            MainUI.mediaHandler.playOrPause();

        }
        if (action.equalsIgnoreCase("com.mridx.freemusic.ACTION_PLAY_TOP")) {

            MainUI.mediaHandler.Play();

        }
    }

}

