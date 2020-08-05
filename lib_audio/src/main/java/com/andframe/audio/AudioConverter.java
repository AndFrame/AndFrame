package com.andframe.audio;

import android.content.Context;
import android.util.Log;

import com.andframe.audio.callback.IConvertCallback;
import com.andframe.audio.callback.ILoadCallback;
import com.andframe.audio.model.AudioFormat;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class AudioConverter {

    private static boolean loaded;

    private Context context;
    private File audioFile;
    private List<File> audioFiles;
    private File destFile;
    private AudioFormat format;
    private IConvertCallback callback;

    private AudioConverter(Context context) {
        this.context = context;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void load(Context context, final ILoadCallback callback) {
        try {
            FFmpeg.getInstance(context).loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    loaded = true;
                    callback.onSuccess();
                }

                @Override
                public void onFailure() {
                    loaded = false;
                    callback.onFailure(new Exception("Failed to loaded FFmpeg lib"));
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (Exception e) {
            loaded = false;
            callback.onFailure(e);
        }
    }

    public static AudioConverter with(Context context) {
        return new AudioConverter(context);
    }

    public AudioConverter setFile(File originalFile) {
        this.audioFile = originalFile;
        return this;
    }

    public AudioConverter setDestFile(File destFile) {
        this.destFile = destFile;
        return this;
    }

    public AudioConverter setFiles(List<File> audioFiles) {
        this.audioFiles = audioFiles;
        return this;
    }


    public AudioConverter setFormat(AudioFormat format) {
        this.format = format;
        return this;
    }

    public AudioConverter setCallback(IConvertCallback callback) {
        this.callback = callback;
        return this;
    }

    public void convert() {
        if (!isLoaded()) {
            callback.onFailure(new Exception("FFmpeg not loaded"));
            return;
        }
        if (audioFile == null || !audioFile.exists()) {
            callback.onFailure(new IOException("File not exists"));
            return;
        }
        if (!audioFile.canRead()) {
            callback.onFailure(new IOException("Can't read the file. Missing permission?"));
            return;
        }
        final File convertedFile = destFile == null ? getConvertedFile(audioFile, format) : destFile;
        final String[] cmd = new String[]{"-y", "-i", audioFile.getPath(), convertedFile.getPath()};
        try {
            FFmpeg.getInstance(context).execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(String message) {

                }

                @Override
                public void onSuccess(String message) {
                    callback.onSuccess(convertedFile);
                }

                @Override
                public void onFailure(String message) {
                    callback.onFailure(new IOException(message));
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    private static File getConvertedFile(File originalFile, AudioFormat format) {
        String[] f = originalFile.getPath().split("\\.");
        String filePath = originalFile.getPath().replace(f[f.length - 1], format.getFormat());
        return new File(filePath);
    }


    public void merge() {
        if (!isLoaded()) {
            callback.onFailure(new Exception("FFmpeg not loaded"));
            return;
        }
        if (audioFiles == null) {
            callback.onFailure(new IOException("audioFiles can not be null"));
            return;
        }

        StringBuffer sb = new StringBuffer();
        for (File file : audioFiles) {
            sb.append("file '").append(file.getAbsolutePath()).append("'").append("\n");
        }
        File audioListFile = new File(context.getCacheDir(), "audio.txt");

        FileWriter writer = null;
        try {
            writer = new FileWriter(audioListFile);
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        String[] cmds = new String[]{"-y", "-f", "concat", "-safe", "0", "-i", audioListFile.getAbsolutePath(), "-codec:a", "libmp3lame", "-qscale:a", "4", destFile.getAbsolutePath()};
        if (BuildConfig.DEBUG) {
            StringBuffer sb2 = new StringBuffer();
            for (String str : cmds) {
                sb2.append(str);
            }
            Log.d("XTAG", "merge audio cmd>" + sb2.toString());
        }
        try {
            FFmpeg.getInstance(context).execute(cmds, new FFmpegExecuteResponseHandler() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(String message) {

                }

                @Override
                public void onSuccess(String message) {
                    if (callback != null) {
                        callback.onSuccess(destFile);
                    }

                }

                @Override
                public void onFailure(String message) {
                    if (callback != null) {
                        callback.onFailure(new IOException(message));
                    }
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(e);
            }
        }
    }

}