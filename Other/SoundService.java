package tech.hypermiles.hypermiles.Other;

import android.content.Context;
import android.media.MediaPlayer;

import tech.hypermiles.hypermiles.R;

/**
 * Created by Asia on 2017-04-04.
 */

public class SoundService {

    private MediaPlayer mediaPlayer;

    public SoundService(Context context)
    {
        mediaPlayer = MediaPlayer.create(context, R.raw.sound);
    }

    public void play()
    {
        if(!Settings.PLAY_SOUND) return;
        mediaPlayer.start();
    }

}
