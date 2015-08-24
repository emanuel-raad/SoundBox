package com.example.emanuel.soundbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emanuel on 8/18/15.
 */
public class BeatBox {
    private static final String TAG = "BeatBox";

    private static final int MAX_SOUNDS = 5;

    private List<Sound> mSounds;
    private SoundPool mSoundPool;

    private String mSoundsFolder;

    public BeatBox(String folderName) {
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        mSoundsFolder = folderName;
        loadSounds();
    }

    private void loadSounds() {
        List<String> filesInDirectory;
        try {
            filesInDirectory = DirectoryHelper.listFilesInDir(mSoundsFolder);
        } catch (IOException ioe) {
            Log.e(TAG, "Could not list assets", ioe);
            return;
        }

        mSounds = new ArrayList<>();
        for (String filename : filesInDirectory) {
            try{
                String assetPath = DirectoryHelper.getDirLocation(mSoundsFolder) + "/" + filename;
                Sound sound = new Sound(assetPath);
                load(sound);
                mSounds.add(sound);
            } catch (IOException ioe) {
                Log.e(TAG, "Could not load sound " + filename, ioe);
            }
        }
    }

    private void load(Sound sound) throws IOException {
        String path = sound.getExternalPath();
        int soundId = mSoundPool.load(path, 1);
        sound.setSoundId(soundId);
    }

    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) {
            return;
        }
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void release() {
        mSoundPool.release();
    }

    public List<Sound> getSounds() {
        return mSounds;
    }

    public String getSoundsFolder() {
        return mSoundsFolder;
    }
}
