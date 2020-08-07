package com.robinx.audio;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class AudioActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        Util.requestSDCardPermission(this);
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

        final List<File> files = new ArrayList<>();
        files.add(new File(dir, "hint.mp3"));
        files.add(new File(dir, "please.mp3"));

        for (int i = 0; i < 5; i++) {
            files.add(new File(dir, (1 + rnd.nextInt(9)) + ".mp3"));
        }

        files.add(new File(dir, "number.mp3"));
        files.add(new File(dir, "goto.mp3"));
        files.add(new File(dir, (rnd.nextBoolean() ? wins[rnd.nextInt(wins.length)] : (1 + rnd.nextInt(9)) + ".mp3")));
        files.add(new File(dir, "window.mp3"));

        final File destFile = new File(dir, "result.mp3");

        StringBuffer sb = new StringBuffer();
        for (File file : files) {
            sb.append("file '").append(file.getAbsolutePath()).append("'").append("\n");
        }
        File audioListFile = new File(getExternalCacheDir(), "merged_audio_tmp.txt");

        Util.writeFile(audioListFile, sb.toString());

        final long t1 = System.currentTimeMillis();
        String[] cmds = new String[]{"-y", "-f", "concat", "-safe", "0", "-i", audioListFile.getAbsolutePath(), "-codec:a", "libmp3lame", "-qscale:a", "4", destFile.getAbsolutePath()};
        FFmpeg.executeAsync(cmds, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                long t2 = System.currentTimeMillis();
                Log.d("XTAG", String.format("exec res code>%s,cost time:%s", returnCode, (t2 - t1)));
            }
        });

    }
}
