package com.andframe.andframe;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andframe.audio.AudioConverter;
import com.andframe.audio.callback.IConvertCallback;
import com.andframe.audio.model.AudioFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Description
 * @Author Robin
 * @Time 2020/8/5 14:56
 */
public class AudioActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        Util.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void onClickMergeAudio(View view) {

        File dir = new File(Environment.getExternalStorageDirectory(), "sound");

        Random rnd = new Random();

        String[] wins = {
                "customer.mp3",
                "army.mp3",
                "children.mp3",
                "women.mp3",
                "old.mp3",
        };

        List<File> files = new ArrayList<>();
        files.add(new File(dir, "hint.mp3"));
        files.add(new File(dir, "please.mp3"));

        for (int i = 0; i < 5; i++) {
            files.add(new File(dir, (1 + rnd.nextInt(9)) + ".mp3"));
        }

        files.add(new File(dir, "number.mp3"));
        files.add(new File(dir, "goto.mp3"));
        files.add(new File(dir, (rnd.nextBoolean() ? wins[rnd.nextInt(wins.length)] : (1 + rnd.nextInt(9)) + ".mp3")));
        files.add(new File(dir, "window.mp3"));

        File destFile = new File(dir, "result.mp3");
        AudioConverter.with(this)
                .setFiles(files)
                .setDestFile(destFile)
                .setCallback(new IConvertCallback() {
                    @Override
                    public void onSuccess(File convertedFile) {
                        Toast.makeText(AudioActivity.this, "SUCCESS: " + convertedFile.getPath(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Exception error) {
                        Log.d("XTAG", "ERROR: " + error.getMessage());
                    }
                })
                .merge();


    }
}
