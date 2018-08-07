package com.halo.redpacket;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;

/**
 * Description:
 */

public class RedpacketHelper {

    /**
     * 播放摇一摇声音
     * @param context
     */
    public static void playShakeSound(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.shake);
        mediaPlayer.start();
    }

    /**
     * 播放摇一摇声音
     * @param context
     */
    public static void playSound(Context context) {
        SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
        int soundID = soundPool.load(context, R.raw.shake, 1);
        soundPool.play(soundID, 1, 1, 0, 0, 1);
    }

    /**
     * 震动
     * @param context
     * @param milliseconds
     */
    public static void vibrite(Context context, long milliseconds) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }
}
