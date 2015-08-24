package com.example.emanuel.soundbox;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emanuel on 24/08/2015.
 */
public class SoundPackLab {

    private ArrayList<SoundPack> mSoundPacks;
    private static SoundPackLab sSoundPackLab;

    public SoundPackLab(Context c) {
        try {
            List<String> folder = DirectoryHelper.listDirsInFolder("beatbox");
            mSoundPacks = new ArrayList<>();
            for (int i = 0; i < folder.size(); i++) {
                SoundPack soundPack = new SoundPack(folder.get(i));
                mSoundPacks.add(soundPack);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SoundPackLab get(Context c) {
        if (sSoundPackLab == null)
            sSoundPackLab = new SoundPackLab(c.getApplicationContext());
        return sSoundPackLab;
    }

    public void addSoundPack(SoundPack s) {
        DirectoryHelper.createDirectory("beatbox", s.getDirectory());
        mSoundPacks.add(s);
    }

    public void deleteSoundPack(SoundPack s) {
        DirectoryHelper.deleteDirectory(new File(s.getFullDirectory()));
        mSoundPacks.remove(s);
    }

    public ArrayList<SoundPack> getSoundPacks() {
        return mSoundPacks;
    }

    public void setSoundPacks(ArrayList<SoundPack> soundPacks) {
        mSoundPacks = soundPacks;
    }

    public SoundPack getSoundPackByFile(File file) {
        for (SoundPack soundPack : mSoundPacks) {
            if (soundPack.getFullDirectory() == file.toString())
                return soundPack;
        }
        return null;
    }

}
