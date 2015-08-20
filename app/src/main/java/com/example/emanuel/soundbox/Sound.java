package com.example.emanuel.soundbox;

/**
 * Created by emanuel on 8/18/15.
 */
public class Sound {

    private String mAssetPath;
    private String mName;
    private Integer mSoundId; // An  Integer can have a null value, while an int can't

    public Sound(String assetPath) {
        mAssetPath = assetPath;
        String[] components = assetPath.split("/");
        String filename = components[components.length - 1];
        //mName = filename.replace(".wav", "");
        mName = filename.replace(".m4a", "");
    }

    public String getAssetPath() {
        return mAssetPath;
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
}
