package com.example.emanuel.soundbox;

/**
 * Created by emanuel on 8/18/15.
 */
public class Sound {

    private String mName;
    private Integer mSoundId; // An  Integer can have a null value, while an int can't
    private String mExternalPath;

    public Sound(String assetPath) {
        mExternalPath = assetPath;
        String[] components = assetPath.split("/");
        String filename = components[components.length - 1];
        mName = DirectoryHelper.removeExtension(filename);
    }

    public String getName() {
        return mName;
    }

    public Integer getSoundId() {
        return mSoundId;
    }

    public void setSoundId(Integer soundId) {
        mSoundId = soundId;
    }

    public String getExternalPath() {
        return mExternalPath;
    }

}
