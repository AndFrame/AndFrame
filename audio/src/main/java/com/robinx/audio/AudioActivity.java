package com.robinx.audio;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

/**
 *
 */
public class AudioActivity extends Activity {

    File soundDirFile = new File(Environment.getExternalStorageDirectory(), "sound");

    Random random = new Random();

    String[] windowNos = {
            "customer.mp3",
            "army.mp3",
            "children.mp3",
            "women.mp3",
            "old.mp3",
    };

    final List<File> audioFileList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        Util.requestSDCardPermission(this);
    }

    public void onClickMergeAudio(View view) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                long t1 = System.currentTimeMillis();
                for (int i = 1; i <= 100; i++) {
                    File destFile = new File(soundDirFile.getAbsolutePath() + File.separator + "result" + File.separator + "result" + i + ".mp3");
                    destFile.getParentFile().mkdirs();
                    randomMergeAudio(destFile);
                }
                long t2 = System.currentTimeMillis();
                Log.d("XTAG", String.format("merge all audio complete, cost time total:%s", (t2 - t1)));
            }
        });

    }

    private void randomMergeAudio(File destFile) {
        audioFileList.clear();
        audioFileList.add(new File(soundDirFile, "hint.mp3"));
        audioFileList.add(new File(soundDirFile, "please.mp3"));

        for (int i = 0; i < 5; i++) {
            audioFileList.add(new File(soundDirFile, (1 + random.nextInt(9)) + ".mp3"));
        }

        audioFileList.add(new File(soundDirFile, "number.mp3"));
        audioFileList.add(new File(soundDirFile, "goto.mp3"));
        audioFileList.add(new File(soundDirFile, (random.nextBoolean() ? windowNos[random.nextInt(windowNos.length)] : (1 + random.nextInt(9)) + ".mp3")));
        audioFileList.add(new File(soundDirFile, "window.mp3"));

        StringBuffer sb = new StringBuffer();
        for (File file : audioFileList) {
            sb.append("file '").append(file.getAbsolutePath()).append("'").append("\n");
        }
        File audioConfigFile = new File(getExternalCacheDir(), "merged_audio_tmp.txt");

        Util.writeFile(audioConfigFile, sb.toString());

        long t1 = System.currentTimeMillis();
        String[] cmds = new String[]{"-y", "-f", "concat", "-safe", "0", "-i", audioConfigFile.getAbsolutePath(), "-codec:a", "libmp3lame", "-qscale:a", "4", destFile.getAbsolutePath()};
        int code = FFmpeg.execute(cmds);
        long t2 = System.currentTimeMillis();
        Log.d("XTAG", String.format("exec res code>%s,cost time:%s", code, (t2 - t1)));
    }
}
