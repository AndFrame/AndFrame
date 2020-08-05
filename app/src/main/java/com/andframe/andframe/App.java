package com.andframe.andframe;

import android.app.Application;
import android.util.Log;

import com.andframe.audio.AudioConverter;
import com.andframe.audio.callback.ILoadCallback;

/**
 *
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                Log.d("XTAG", "AudioConverter load success");
            }

            @Override
            public void onFailure(Exception error) {
                Log.d("XTAG", "AudioConverter load error:" + (error == null ? "null" : error.getMessage()));
                if (error != null) {
                    error.printStackTrace();
                }

            }
        });
    }
}
